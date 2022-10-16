/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.transform

import groovy.transform.CompileStatic
import groovy.transform.Generated
import groovy.transform.NamedParam
import groovy.transform.NamedVariant
import groovyjarjarasm.asm.Handle
import groovyjarjarasm.asm.MethodVisitor
import groovyjarjarasm.asm.Type as JarType
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.tools.GeneralUtils
import org.jetbrains.annotations.ApiStatus
import org.objectweb.asm.Opcodes

import javax.annotation.Nullable

import static org.objectweb.asm.Opcodes.*

@CompileStatic
@ApiStatus.Internal
class TransformUtils {
    static final AnnotationNode GENERATED_ANNOTATION = new AnnotationNode(ClassHelper.make(Generated))

    static final int CONSTANT_MODIFIERS = ACC_PRIVATE | ACC_STATIC | ACC_FINAL

    @NamedVariant
    static MethodNode addStaticMethod(@NamedParam(required = true) final ClassNode targetClassNode,
                                      @NamedParam(required = true) final String methodName,
                                      @NamedParam final int modifiers = ACC_PUBLIC | ACC_STATIC,
                                      @NamedParam final ClassNode returnType = ClassHelper.VOID_TYPE,
                                      @NamedParam final Parameter[] parameters = Parameter.EMPTY_ARRAY,
                                      @NamedParam final ClassNode[] exceptions = ClassNode.EMPTY_ARRAY,
                                      @NamedParam final List<AnnotationNode> annotations = [GENERATED_ANNOTATION],
                                      @NamedParam final Statement code = new BlockStatement(),
                                      @NamedParam @Nullable final Closure<Statement> conditionalCode = null) {

        addMethod(targetClassNode, methodName, modifiers, returnType, parameters, exceptions, annotations, code, conditionalCode)
    }

    @NamedVariant
    static MethodNode addMethod(@NamedParam(required = true) final ClassNode targetClassNode,
                                @NamedParam(required = true) final String methodName,
                                @NamedParam final int modifiers = ACC_PUBLIC,
                                @NamedParam final ClassNode returnType = ClassHelper.VOID_TYPE,
                                @NamedParam final Parameter[] parameters = Parameter.EMPTY_ARRAY,
                                @NamedParam final ClassNode[] exceptions = ClassNode.EMPTY_ARRAY,
                                @NamedParam final List<AnnotationNode> annotations = [GENERATED_ANNOTATION],
                                @NamedParam final Statement code = new BlockStatement(),
                                @NamedParam @Nullable final Closure<Statement> conditionalCode = null) {
        if (annotations) {
            final MethodNode maybeExistingMethod = targetClassNode.getDeclaredMethod(methodName, parameters)
            if (maybeExistingMethod !== null) return maybeExistingMethod

            final MethodNode method = new MethodNode(methodName, modifiers, returnType, parameters, exceptions, conditionalCode?.call() ?: code)
            method.addAnnotations(annotations)
            targetClassNode.addMethod(method)

            return method
        } else {
            return targetClassNode.addMethod(methodName, modifiers, returnType, parameters, exceptions, conditionalCode?.call() ?: code)
        }
    }

    @NamedVariant
    static FieldNode addField(@NamedParam(required = true) final ClassNode targetClassNode,
                              @NamedParam(required = true) final String fieldName,
                              @NamedParam final int modifiers = ACC_PUBLIC, // note: public fields aren't always properties
                              @NamedParam final ClassNode type = ClassHelper.OBJECT_TYPE,
                              @NamedParam @Nullable final Closure<ClassNode> conditionalType = null,
                              @NamedParam final List<AnnotationNode> annotations = [GENERATED_ANNOTATION],
                              @NamedParam final Expression initialValue = null,
                              @NamedParam @Nullable final Closure<Expression> conditionalInitialValue = null,
                              @NamedParam final boolean addFieldFirst = false) {
        if (annotations) {
            final FieldNode field = new FieldNode(fieldName, modifiers, conditionalType?.call() ?: type, targetClassNode, conditionalInitialValue?.call() ?: initialValue)
            field.addAnnotations(annotations)

            if (addFieldFirst) targetClassNode.addFieldFirst(field)
            else targetClassNode.addField(field)

            return field
        } else {
            if (addFieldFirst) return targetClassNode.addFieldFirst(fieldName, modifiers, conditionalType?.call() ?: type, conditionalInitialValue?.call() ?: initialValue)
            else return targetClassNode.addField(fieldName, modifiers, conditionalType?.call() ?: type, conditionalInitialValue?.call() ?: initialValue)
        }
    }

    static boolean isSubclass(final ClassNode target, final ClassNode superType) {
        if (superType.isInterface()) {
            return GeneralUtils.isOrImplements(target, superType)
        } else {
            return target.isDerivedFrom(superType)
        }
    }

    static ConstructorNode getOrCreatorCtor(final ClassNode classNode) {
        return classNode.declaredConstructors.find {
            !it.firstStatementIsSpecialConstructorCall()
        } ?: classNode.addConstructor(ACC_PUBLIC, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, new BlockStatement())
    }

    static void addLastCtorStatement(final ClassNode classNode, final Statement statement) {
        (getOrCreatorCtor(classNode).code as BlockStatement).addStatement(statement)
    }

    static void indy(MethodVisitor visitor, ClassNode functionalInterface, MethodNode lambdaMethod, ClassNode declaringType) {
        final method = functionalInterface.methods.find { it.isAbstract() } ?: getAllMethods(functionalInterface).find { it.isAbstract() }
        visitor.visitInvokeDynamicInsn(
                method.name, "()L${getInternalName(functionalInterface)};",
                new Handle(Opcodes.H_INVOKESTATIC, 'java/lang/invoke/LambdaMetafactory', 'metafactory', '(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;', false),
                new Object[] {
                    getDesc(method),
                    new Handle(declaringType.interface ? Opcodes.INVOKEINTERFACE : (lambdaMethod.isStatic() ? Opcodes.INVOKESTATIC : Opcodes.INVOKEVIRTUAL), getInternalName(declaringType), lambdaMethod.name, getDesc(lambdaMethod).getDescriptor(), declaringType.isInterface()),
                    getDesc(lambdaMethod)
                }
        )
    }

    static List<MethodNode> getAllMethods(ClassNode classNode) {
        final methods = new ArrayList<>(classNode.methods)
        if (classNode.superClass !== null) {
            methods.addAll(getAllMethods(classNode.superClass))
        }
        for (final it in classNode.interfaces) {
            methods.addAll(getAllMethods(it))
        }
        return methods
    }

    private static String getInternalName(ClassNode classNode) {
        return classNode.name.replace('.' as char, '/' as char)
    }

    private static JarType getDesc(MethodNode method) {
        JarType.getMethodType(getType(method.returnType),
                Arrays.stream(method.parameters).map {getType(it.type) }.toArray(JarType[]::new))
    }

    private static JarType getType(ClassNode classNode) {
        return switch (classNode) {
            case ClassHelper.int_TYPE -> JarType.INT_TYPE
            case ClassHelper.double_TYPE -> JarType.DOUBLE_TYPE
            case ClassHelper.boolean_TYPE -> JarType.BOOLEAN_TYPE
            case ClassHelper.float_TYPE -> JarType.FLOAT_TYPE
            case ClassHelper.short_TYPE -> JarType.SHORT_TYPE
            case ClassHelper.byte_TYPE -> JarType.BYTE_TYPE
            case ClassHelper.VOID_TYPE -> JarType.VOID_TYPE
            case ClassHelper.long_TYPE -> JarType.LONG_TYPE
            case ClassHelper.char_TYPE -> JarType.CHAR_TYPE
            default -> JarType.getObjectType(getInternalName(classNode))
        }
    }
}
