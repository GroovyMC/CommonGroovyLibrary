/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.transform.registroid

import com.google.common.base.Suppliers
import groovy.transform.CompileStatic
import groovyjarjarasm.asm.Handle
import groovyjarjarasm.asm.Type as JarType
import io.github.groovymc.cgl.reg.RegistrationProvider
import io.github.groovymc.cgl.reg.RegistryObject
import io.github.groovymc.cgl.transform.TransformUtils
import io.github.groovymc.cgl.transform.util.ModClassTransformer
import io.github.groovymc.cgl.transform.util.ModIdRequester
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.tools.GeneralUtils
import org.codehaus.groovy.ast.tools.GenericsUtils
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.TransformWithPriority
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

import java.util.function.Predicate
import java.util.function.Supplier

@CompileStatic
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
final class RegistroidASTTransformer extends AbstractASTTransformation implements TransformWithPriority {
    public static final List<ClassNode> REGISTERED_CLASSES = []

    public static final ClassNode REGISTRATION_NAME_TYPE = ClassHelper.make(RegistrationName)
    public static final String REGISTER_DESC = Type.getMethodDescriptor(Type.getType(RegistryObject), Type.getType(String), Type.getType(Supplier))
    public static final String DR_REGISTER_DESC = Type.getMethodDescriptor(Type.getObjectType('net/minecraftforge/registries/RegistryObject'), Type.getType(String), Type.getType(Supplier))
    public static final ClassNode RESOURCE_KEY_TYPE = ClassHelper.make(ResourceKey)
    public static final Supplier<ClassNode> FORGE_REGISTRY_TYPE = Suppliers.memoize { ClassHelper.make('net.minecraftforge.registries.IForgeRegistry') }
    public static final Supplier<ClassNode> FORGE_RO_TYPE = Suppliers.memoize { ClassHelper.make('net.minecraftforge.registries.RegistryObject') }
    public static final ClassNode REGISTRATION_PROVIDER_TYPE = ClassHelper.make(RegistrationProvider)
    public static final ClassNode REG_OBJECT_TYPE = ClassHelper.make(RegistryObject)
    public static final ClassNode ADDON_CLASS_TYPE = ClassHelper.make(RegistroidAddonClass)
    public static final ClassNode REGISTRY_TYPE = ClassHelper.make(Registry)

    public static final String REG_OBJECT_INTERNAL = Type.getInternalName(RegistryObject)
    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        init(nodes, source) // ensure that nodes is [AnnotationNode, AnnotatedNode]

        final AnnotationNode annotation = nodes[0] as AnnotationNode
        final AnnotatedNode targetNode = nodes[1] as AnnotatedNode

        if (targetNode instanceof ClassNode) {
            transformClass(annotation, targetNode, targetNode.annotations.stream().flatMap {
                it.classNode.annotations.stream()
            }.filter { it.classNode == ADDON_CLASS_TYPE }
            .map { Class.forName(getMemberClassValue(it, 'value').name, true, getClass().classLoader) }
            .map { it.getDeclaredConstructor().newInstance() as RegistroidAddon }.toList())
            return
        }

        // make sure the annotation is only applied to fields / classes
        if (!(targetNode instanceof FieldNode)) {
            addError("The ${annotation.classNode.name} annotation can only be applied to fields and classes.", targetNode)
            return
        }
        final targetField = (FieldNode) targetNode
        final targetClass = (ClassNode) targetField.declaringClass
        final modId = getMemberStringValue(annotation, 'modId') ?: ModIdRequester.getModId(targetClass.packageName)
        transformField(annotation, targetClass, targetField, modId)
    }

    private void transformClass(AnnotationNode annotationNode, ClassNode targetClass, List<RegistroidAddon> addons) {
        final closure = annotationNode.getMember('value') as ClosureExpression
        Expression expression
        if (closure === null) {
            // One may use it for addons only
            expression = new ListExpression()
        } else {
            expression = (((BlockStatement) closure.code).statements.find() as ExpressionStatement).expression
        }

        // Collect information about existing DR fields, in order to prevent addons from creating duplicate registries
        final existingDRFields = targetClass.fields.stream()
                .filter { it.type == REGISTRATION_PROVIDER_TYPE || it.type.name == 'net.minecraftforge.registries.DeferredRegister' }
                .toList()
        final existingDRs = existingDRFields.stream()
                .map { it.type.genericsTypes[0].type }.toList()

        final modId = getMemberStringValue(annotationNode, 'modId') ?: ModIdRequester.getModId(targetClass.packageName)
        if (modId === null) {
            addError('Could not determine modId for @Registroid. Consider implementing a custom ModIdRequester$Helper or specify the modId in the annotation.', targetClass)
            return
        }

        List<FieldNode> drFields = []

        // Collect and create DR fields for the type(s) specified in the closure
        if (expression instanceof ListExpression) {
            expression.expressions.each {
                final val = it as PropertyExpression
                final property = (val.property as ConstantExpression).value as String
                final registryField = (val.getObjectExpression() as ClassExpression).type.getField(property)
                drFields.push(generateDR(targetClass, registryField, modId))
            }
        } else {
            final val = expression as PropertyExpression
            final declaringClass = (val.getObjectExpression() as ClassExpression).type
            final property = (val.property as ConstantExpression).value as String
            final registryField = declaringClass.getField(property)
            drFields.push(generateDR(targetClass, registryField, modId))
        }

        // Now allow addons to create registries
        addons.stream().flatMap {it.requiredRegistries.stream()}.forEach {
            final declaringClass = (it.getObjectExpression() as ClassExpression).type
            final property = (it.property as ConstantExpression).value as String
            final registryField = declaringClass.getField(property)
            final dr = generateDR(targetClass, registryField, modId, false)
            final drGenericType = dr.type.genericsTypes[0].type
            // Make sure the addond doesn't create duplicate registries
            if (!drFields.any { it.type.genericsTypes[0].type == drGenericType || it.name == dr.name } && !existingDRs.contains(drGenericType)) {
                drFields.push(dr)
                targetClass.addField(dr)
            }
        }

        final Map<ClassNode, String> drToModId = [:]
        // Collect a map of DR type -> DR modId
        (drFields + existingDRFields).each {
            if (it.initialValueExpression instanceof StaticMethodCallExpression) {
                final args = (it.initialValueExpression as StaticMethodCallExpression).arguments
                if (args instanceof ArgumentListExpression && args.size() == 2 && args.getExpression(1) instanceof ConstantExpression) {
                    final val = (args.getExpression(1) as ConstantExpression).value
                    if (val instanceof String) {
                        drToModId[it.type.genericsTypes[0].type] = val
                    }
                }
            }
        }

        // Now allow the addons to do their thing, before we run the RO transformation
        List.copyOf(targetClass.properties).each { PropertyNode prop ->
            addons.each {
                final found = it.supportedTypes.find { TransformUtils.isSubclass(prop.type, it) }
                if (found !== null) {
                    it.process(annotationNode, targetClass, prop, this, () -> recursivelyFind(found, drToModId))
                }
            }
        }

        // Run addons over inners as well, if inners are included
        if (getMemberValue(annotationNode, 'includeInnerClasses')) {
            targetClass.innerClasses.each {
                registerInnerAddons(annotationNode, targetClass, it, drToModId, addons)
            }
        }

        // Filter out null DR fields
        drFields = drFields.stream().filter { it !== null }.toList()
        // Make sure no duplicates are found, and if so, error
        if (hasDuplicates(drFields, targetClass, existingDRs)) return
        // And now go over each added DR field, and process it
        drFields.each {
            transformField(annotationNode, targetClass, it, modId)
        }
    }

    private void registerInnerAddons(final AnnotationNode annotationNode, final ClassNode targetClass, final InnerClassNode innerClass,
                                     final Map<ClassNode, String> drToModIds, final List<RegistroidAddon> addons) {
        // Only run over static inners
        if (!innerClass.isStaticClass()) return
        // Run the addons over each property
        List.copyOf(innerClass.properties).each { PropertyNode prop ->
            addons.each {
                final found = it.supportedTypes.find { TransformUtils.isSubclass(prop.type, it) }
                if (found !== null) {
                    it.process(annotationNode, targetClass, prop, this, () -> recursivelyFind(found, drToModIds))
                }
            }
        }
        // And recursively register inners of inners
        innerClass.innerClasses.each { registerInnerAddons(annotationNode, targetClass, it, drToModIds, addons) }
    }

    private boolean hasDuplicates(List<FieldNode> fields, ClassNode targetClass, List<ClassNode> existingTypes) {
        final Set<ClassNode> types = new HashSet<>()
        types.addAll(existingTypes)
        for (final field : fields) {
            // DeferredRegister<T>
            final drType = field.type.genericsTypes[0].type
            if (drType in types) {
                addError("Registroid ambiguity: Found multiple DeferredRegisters pointing to the same type, ${drType.name}", targetClass)
                return true
            }
            types.add(drType)
        }
        return false
    }

    private FieldNode generateDR(final ClassNode targetClass, final FieldNode registryField, final String modId, boolean addToClass = true) {
        FieldNode field
        if (registryField.type == RESOURCE_KEY_TYPE) {
            field = makeResourceKey(targetClass, registryField, modId)
        } else if (registryField.type.isDerivedFrom(REGISTRY_TYPE)) {
            field = makeRegistry(targetClass, registryField, modId)
        } else if (GeneralUtils.isOrImplements(registryField.type, FORGE_REGISTRY_TYPE.get())) {
            field = makeForgeRegistry(targetClass, registryField, modId)
        } else {
            addError("No known way of representing registry from ${registryField.owner.name}.${registryField.name}", registryField)
            return null
        }

        field.addAnnotation(TransformUtils.GENERATED_ANNOTATION)
        if (addToClass) {
            targetClass.addField(field)
        }
        return field
    }

    private static FieldNode makeResourceKey(final ClassNode targetClass, final FieldNode registryField, final String modId) {
        // ResourceKey<Registry<T>>
        final type = registryField.type.genericsTypes[0].type.genericsTypes[0].type
        return new FieldNode(
                registryField.name.replace('_REGISTRY', 'S').toUpperCase(Locale.ROOT),
                Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                GenericsUtils.makeClassSafeWithGenerics(RegistrationProvider, type), targetClass,
                GeneralUtils.callX(
                        REGISTRATION_PROVIDER_TYPE, 'get', GeneralUtils.args(
                        GeneralUtils.fieldX(registryField), GeneralUtils.constX(modId)
                    )
                )
        )
    }

    private static FieldNode makeForgeRegistry(final ClassNode targetClass, final FieldNode registryField, final String modId) {
        // IForgeRegistry<T>
        final type = registryField.type.genericsTypes[0].type
        return new FieldNode(
                registryField.name.toUpperCase(Locale.ROOT),
                Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                GenericsUtils.makeClassSafeWithGenerics(RegistrationProvider, type),
                targetClass, GeneralUtils.callX(
                REGISTRATION_PROVIDER_TYPE, 'get', GeneralUtils.args(
                    GeneralUtils.callX(GeneralUtils.fieldX(registryField), 'getRegistryKey'), GeneralUtils.constX(modId)
                )
            )
        )
    }

    private static FieldNode makeRegistry(final ClassNode targetClass, final FieldNode registryField, final String modId) {
        // Registry<T>
        final type = registryField.type.genericsTypes[0].type
        return new FieldNode(
                (registryField.name + 'S').toUpperCase(Locale.ROOT),
                Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                GenericsUtils.makeClassSafeWithGenerics(RegistrationProvider, type), targetClass,
                GeneralUtils.callX(
                        REGISTRATION_PROVIDER_TYPE, 'get', GeneralUtils.args(
                        GeneralUtils.fieldX(registryField), GeneralUtils.constX(modId)
                    )
                )
        )
    }

    private void transformField(AnnotationNode annotation, ClassNode targetClass, FieldNode targetField, String modId) {
        if (!targetField.isStatic()) {
            addError('@Registroid can only be applied to static fields.', targetField)
            return
        }
        if (targetField.type != REGISTRATION_PROVIDER_TYPE && targetField.type.name != 'net.minecraftforge.registries.DeferredRegister') {
            addError('@Registroid can only be applied to fields of either the type DeferredRegister or RegistrationProvider', targetField)
            return
        }
        // Make the DR public, if it isn't
        if (!targetField.isPublic()) {
            final wasFinal = targetField.isFinal()
            targetField.modifiers = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC
            if (wasFinal) {
                targetField.modifiers = targetField.modifiers | Opcodes.ACC_FINAL
            }
        }

        final baseType = targetField.type.genericsTypes.size() == 0 ? ClassHelper.OBJECT_TYPE : targetField.type.genericsTypes[0].type

        // Check if the field is a subclass of the DR type
        final Predicate<PropertyNode> predicate = { PropertyNode it -> TransformUtils.isSubclass(it.type, baseType) && it.isStatic() /* Only process static fields */ }

        final isForge = targetField.type != REGISTRATION_PROVIDER_TYPE

        // Process registry objects
        targetClass.properties.each {
            if (predicate.test(it)) {
                register(baseType, targetField, targetClass, it, isForge)
            }
        }

        // If we include inners as well, try to register objects from those as well
        if (getMemberValue(annotation, 'includeInnerClasses')) {
            registerInners(predicate, targetClass, baseType, targetField)
        }

        // And finally, if the annotation wants us to register the DR to the bus, do it
        if ((getMemberValue(annotation, 'registerToBus') ?: true) && modId) {
            if (targetField.owner !in REGISTERED_CLASSES) {
                ModClassTransformer.registerTransformer(modId) { classNode, annotationNode, source ->
                    targetField.owner.methods.find {
                        it.parameters.size() == 0 && it.name == 'init' && it.isStatic()
                    } ?: TransformUtils.addMethod(
                            targetClassNode: targetField.owner,
                            methodName: 'init',
                            modifiers: Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC
                    )
                    TransformUtils.addLastCtorStatement(classNode, GeneralUtils.stmt(GeneralUtils.callX(
                            targetField.owner, 'init'
                    )))
                }
                REGISTERED_CLASSES.push(targetField.owner)
            }
        }
    }

    private void registerInners(final Predicate<PropertyNode> predicate, final ClassNode targetClass, final ClassNode baseType, final FieldNode targetField) {
        targetClass.innerClasses.each { InnerClassNode inner ->
            // Only run over static inners
            if ((inner.modifiers & Opcodes.ACC_STATIC) == 0) return

            // Register properties of the inner class
            final isForge = targetField.type != REGISTRATION_PROVIDER_TYPE
            inner.properties.each {
                if (predicate.test(it)) {
                    register(baseType, targetField, inner, it, isForge)
                }
            }
            // Add a init method for easier classloading
            if (!inner.methods.any { it.parameters.size() == 0 && it.name == 'init' && it.isStatic() }) {
                TransformUtils.addStaticMethod(
                        targetClassNode: inner,
                        methodName: 'init',
                )
            }

            // To make sure the objects are registered, call the init method we created in clinit of the base class
            targetClass.addStaticInitializerStatements([GeneralUtils.stmt(
                    GeneralUtils.callX(inner, 'init')
            )], false)
            // Finally, recursively register inner classes
            inner.innerClasses.each { InnerClassNode secondInner ->
                registerInners(predicate, inner, baseType, targetField)
            }
        }
    }

    private void register(final ClassNode registryType, final FieldNode drVar, final ClassNode clazz, final PropertyNode property, final boolean isForge) {
        // Force Groovy to go through getters when accessing, and disallow setting
        property.modifiers = TransformUtils.CONSTANT_MODIFIERS

        // Because of the fact that each closure is a new class, which also increases the size of the compiled mod
        // we will use indy + Java lambdas for the supplier used to create the registry object

        // We start by injecting a synthetic lambda method
        /*
         * access: private, static, synthetic
         * params: empty
         * return type: the type of the registry, NOT the type of the property (Item instead of BlockItem)
         */
        final lambdaMethod = clazz.addMethod("registroid\$syn\$${property.name}",
                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC, registryType,
                Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, GeneralUtils.returnS(property.initialExpression))

        // Figure out the generic RegistryObject<T> type
        final regObjectType = GenericsUtils.makeClassSafeWithGenerics(isForge ? FORGE_RO_TYPE.get() : REG_OBJECT_TYPE, new GenericsType(property.type))

        final classInternal = getInternalName(clazz)

        // Create a RegistryObject<T> private static final field, named $registryObjectFor${PROPERTY_NAME}
        final field = TransformUtils.addField(
                targetClassNode: clazz,
                fieldName: "\$registryObjectFor${property.name.capitalize()}",
                modifiers: TransformUtils.CONSTANT_MODIFIERS,
                type: regObjectType,
                // Initialise it with raw bytecode, in order to be able to use indy (see above)
                initialValue: GeneralUtils.bytecodeX(regObjectType) {
                    final drType = getInternalName(drVar.type)
                    it.visitFieldInsn(Opcodes.GETSTATIC, getInternalName(drVar.owner), drVar.name, "L${drType};")
                    it.visitLdcInsn(getRegName(property))
                    final String getDesc = "()L${getInternalName(registryType)};"
                    it.visitInvokeDynamicInsn('get', '()Ljava/util/function/Supplier;',
                            new Handle(Opcodes.H_INVOKESTATIC, 'java/lang/invoke/LambdaMetafactory', 'metafactory', '(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;', false),
                            new Object[] {
                                    JarType.getType('()Ljava/lang/Object;'),
                                    new Handle(Opcodes.H_INVOKESTATIC, classInternal, lambdaMethod.name, getDesc, false),
                                    JarType.getType(getDesc)
                            })
                    it.visitMethodInsn(isForge ? Opcodes.INVOKEVIRTUAL : Opcodes.INVOKEINTERFACE, drType, 'register', isForge ? DR_REGISTER_DESC : REGISTER_DESC, !isForge)
                }
        )

        // Remove the old property
        clazz.removeField(property.field.name)

        // And now set the code of the getter, to use RegistryObject#get
        // For optimization purposes, this is raw bytecode
        property.setGetterBlock(GeneralUtils.stmt(GeneralUtils.bytecodeX(property.type) {
            // (MyFieldType) MyClass.$registryObjectForMY_PROPERTY.get()
            it.visitFieldInsn(Opcodes.GETSTATIC, classInternal, field.name, Type.getDescriptor(RegistryObject))
            it.visitMethodInsn(isForge ? Opcodes.INVOKEVIRTUAL : Opcodes.INVOKEINTERFACE, REG_OBJECT_INTERNAL, 'get', '()Ljava/lang/Object;', !isForge)
            it.visitTypeInsn(Opcodes.CHECKCAST, getInternalName(property.type))
        }))
    }

    String getRegName(final PropertyNode propertyNode) {
        final regNameOnClass = propertyNode.declaringClass.annotations.find { it.classNode == REGISTRATION_NAME_TYPE }
        final boolean alwaysApply = regNameOnClass === null ? false : getMemberValue(regNameOnClass, 'alwaysApply')
        final prefix = regNameOnClass === null ? '' : getMemberStringValue(regNameOnClass, 'value')
        final ann = propertyNode.annotations.find { it.classNode == REGISTRATION_NAME_TYPE }
        if (ann !== null) {
            final name = getMemberStringValue(ann, 'value')
            return alwaysApply ? prefix + name : name
        }
        return prefix + propertyNode.name.toLowerCase(Locale.ROOT)
    }

    @SuppressWarnings('GrMethodMayBeStatic')
    String getInitialRegName(final PropertyNode propertyNode) {
        final ann = propertyNode.annotations.find { it.classNode == REGISTRATION_NAME_TYPE }
        return ann === null ? propertyNode.name.toLowerCase(Locale.ROOT) : getMemberStringValue(ann, 'value')
    }

    private static String getInternalName(ClassNode classNode) {
        return classNode.name.replace('.' as char, '/' as char)
    }

    private static String recursivelyFind(final ClassNode type, final Map<ClassNode, String> map) {
        String val = map[type]
        if (val) return val
        if (type.superClass !== null && (val = recursivelyFind(type.superClass, map)) !== null) {
            return val
        }
        for (final iface : type.interfaces) {
            if ((val = recursivelyFind(iface, map)) !== null) return val
        }
        return null
    }

    @Override
    int priority() {
        return 10
    }
}
