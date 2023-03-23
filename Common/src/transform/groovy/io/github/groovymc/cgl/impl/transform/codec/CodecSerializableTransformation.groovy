/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.impl.transform.codec

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.codec.comments.Comment
import io.github.groovymc.cgl.api.transform.codec.*
import org.apache.groovy.util.BeanUtils
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.tools.GeneralUtils
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.TransformWithPriority
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport

import java.lang.reflect.Modifier
import java.nio.ByteBuffer
import java.util.stream.IntStream
import java.util.stream.LongStream

import static org.codehaus.groovy.ast.ClassHelper.makeWithoutCaching

@CompileStatic
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
class CodecSerializableTransformation extends AbstractASTTransformation implements TransformWithPriority {

    static final ClassNode MY_TYPE = makeWithoutCaching(CodecSerializable)
    static final ClassNode EXPOSES_TYPE = makeWithoutCaching(ExposesCodec)
    static final ClassNode EXPOSES_FACTORY_TYPE = makeWithoutCaching(ExposesCodecFactory)
    static final ClassNode WITH_TYPE = makeWithoutCaching(WithCodec)
    static final ClassNode COMMENT = makeWithoutCaching(Comment)
    static final ClassNode TUPLE_MAP_CODEC = makeWithoutCaching('io.github.groovymc.cgl.api.codec.TupleMapCodec')
    static final ClassNode MAP_COMMENT_SPEC = makeWithoutCaching('io.github.groovymc.cgl.api.codec.comments.MapCommentSpec')
    static final String CODEC = 'com.mojang.serialization.Codec'
    static final ClassNode CODEC_NODE = makeWithoutCaching(CODEC)
    static final ClassNode OPTIONAL = makeWithoutCaching(Optional)

    static final ClassNode BYTE_BUFFER = makeWithoutCaching(ByteBuffer)
    static final ClassNode INT_STREAM = makeWithoutCaching(IntStream)
    static final ClassNode LONG_STREAM = makeWithoutCaching(LongStream)
    static final ClassNode PAIR = makeWithoutCaching('com.mojang.datafixers.util.Pair')
    static final ClassNode EITHER = makeWithoutCaching('com.mojang.datafixers.util.Either')
    static final ClassNode STRING_REPRESENTABLE = makeWithoutCaching('net.minecraft.util.StringRepresentable')

    public static final String DEFAULT_CODEC_PROPERTY = '$CODEC'

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        init(nodes, source)
        AnnotatedNode parent = (AnnotatedNode) nodes[1]
        AnnotationNode anno = (AnnotationNode) nodes[0]
        if (MY_TYPE != anno.getClassNode()) return

        if (parent instanceof ClassNode) {
            doAssembleCodec(parent, anno)
        }
    }

    static String toSnakeCase( String text ) {
        text.replaceAll( /([A-Z])/, /_$1/ ).toLowerCase().replaceAll( /^_/, '' )
    }

    void doAssembleCodec(ClassNode parent, AnnotationNode anno) {
        String fieldName = getMemberValue(anno, "property", DEFAULT_CODEC_PROPERTY)
        if (parent.getField(fieldName)?.static)
            throw new RuntimeException("Codec-serializable class ${parent.name} already defines a static field with the same name as the codec: ${fieldName}")

        ConstructorNode assembler
        if (parent.declaredConstructors.size()>=1)
            assembler = parent.declaredConstructors.sort(false) {
                -it.parameters.size()
            }.get(0)
        else
            throw new RuntimeException('Codec-serializable classes must have at least one constructor.')

        ClassNode resolvedCodec = makeWithoutCaching(CODEC)
        resolvedCodec.setGenericsTypes(new GenericsType(parent))

        Expression[] grouping = new Expression[assembler.parameters.size()]

        for (int i = 0; i < assembler.parameters.size(); i++) {
            grouping[i] = assembleExpression(anno, parent, assembler.parameters[i], getMemberBooleanValue(anno, 'camelToSnake', true))
        }

        // Parse and merge comments
        Map<String,String> comments = [:]
        for (int i = 0; i < assembler.parameters.size(); i++) {
            String name = assembler.parameters[i].name
            AnnotatedNode node = parent.getField(name)
            if (node?.isStatic()) node = null
            String docs = node?.groovydoc?.content
            docs = docs?.with {toCommentFreeText(it)}?.trim()?:''
            if (node !== null) {
                String val = node.getAnnotations(COMMENT).find()?.with {
                    return getMemberStringValue(it, 'value')
                }
                if (val != '' && val !== null)
                    docs = val
            }
            if (docs == '') {
                node = parent.getMethod((ClassHelper.isPrimitiveBoolean(assembler.parameters[i].type) ? "is" : "get") + BeanUtils.capitalize(name))
                if (node?.isStatic()) node = null
                docs = node?.groovydoc?.content
                docs = docs?.with {toCommentFreeText(it)}?.trim()?:''
                if (node !== null) {
                    String val = node.getAnnotations(COMMENT).find()?.with {
                        return getMemberStringValue(it, 'value')
                    }
                    if (val != '' && val !== null)
                        docs = val
                }
            }

            if (docs != '') {
                comments[name] = docs
            }
        }

        if (!comments.isEmpty()) {
            grouping = (new Expression[] {new StaticMethodCallExpression(
                    MAP_COMMENT_SPEC,
                    'of',
                    new ArgumentListExpression(new MapExpression(comments.collect { key, comment ->
                        new MapEntryExpression(new ConstantExpression(key), new ConstantExpression(comment))
                    }))
            )})+grouping
        }
        grouping = (new Expression[] {new MethodReferenceExpression(new ClassExpression(parent), new ConstantExpression('new'))}) + grouping

        Expression grouped = new StaticMethodCallExpression(TUPLE_MAP_CODEC, 'of', new ArgumentListExpression(grouping))
        Expression initialValue = new MethodCallExpression(grouped, 'codec', new ArgumentListExpression())

        ClassNode wrappedNode = makeWithoutCaching(CODEC)
        wrappedNode.redirect = resolvedCodec
        parent.addField(fieldName, Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL, wrappedNode, initialValue)
        List<AnnotationNode> exposes = parent.annotations.findAll {it.classNode == EXPOSES_TYPE}
        if (exposes.size() == 0)
            parent.addAnnotation(new AnnotationNode(EXPOSES_TYPE).tap {
                addMember('value', new ConstantExpression(fieldName))
            })
    }

    boolean getMemberBooleanValue(AnnotationNode node, String name, boolean defaultValue) {
        Object value = getMemberValue(node, name)
        if (value instanceof Boolean) {
            return (Boolean) value
        }
        return defaultValue
    }

    // Credit to Paint_Ninja
    static String toCommentFreeText(String content) {
        // For single line comments:
        // ^(\/\*\*\s?)(?<string>.+)(\s?\*\/) turns '/** ... */' into ...
        final String commentFreeText = content
                .stripIndent()
                .replaceAll($/^(/\*\*\s?)(?<string>.+)(\s?\*/)/$, '$2')

        if (!commentFreeText.contains('*/'))
            return commentFreeText

        // For multi-line comments:
        // ^(/\*\*\s?) turns '/** ' into ''
        // ^(\s\*\s?)(?<string>.+)? turns ' * ...' into '...'
        // ^(\s?\*\/) turns '*/' into ''
        String multiLineCommentFreeText = ''
        boolean firstLine = true
        commentFreeText.eachLine { line ->
            line = line.stripIndent()
                    .replaceAll(/^(\/\*\*\s?)/, '')
                    .replaceAll(/^(\*\s?)(?<string>.+)?/, '$2')
                    .replaceAll($/^(\*/)/$, '')

            if (firstLine) {
                firstLine = false
                multiLineCommentFreeText += line
            } else if (line != '/') {
                multiLineCommentFreeText += '\n' + line
            }

            return
        }

        return multiLineCommentFreeText[1..-1] // trim off the first newline
    }

    Object getMemberValue(AnnotationNode node, String name, Object defaultVal) {
        return getMemberValue(node, name)?:defaultVal
    }

    Expression assembleExpression(AnnotationNode anno, ClassNode parent, Parameter parameter, boolean camelToSnake) {
        String name = parameter.name
        FieldNode field = parent.getField(name)
        if (field?.isStatic()) field = null
        MethodNode getter = parent.getMethod((ClassHelper.isPrimitiveBoolean(parameter.type) ? "is" : "get") + BeanUtils.capitalize(name))
        if (getter?.isStatic()) getter = null
        List<AnnotationNode> annotations = []
        annotations.addAll(parameter.annotations)
        annotations.addAll(field?.annotations?:[])
        annotations.addAll(getter?.annotations?:[])
        List<WithCodecPath> path = (isOptional(parameter.type))?([WithCodecPath.OPTIONAL]):([])
        List<Integer> target = (isOptional(parameter.type))?([0]):([])
        Expression baseCodec = getCodecFromType(unresolveOptional(parameter.type),annotations,path,target)
        Expression fieldOf
        String fieldName = camelToSnake? toSnakeCase(name) : name
        if (!isOptional(parameter.type)) {
            PropertyNode pNode = parent.getProperty(name)
            if (pNode?.isStatic()) pNode = null
            if (getMemberBooleanValue(anno, 'allowDefaultValues', true) && !checkNull(parameter.initialExpression))
                fieldOf = new MethodCallExpression(baseCodec, 'optionalFieldOf', new ArgumentListExpression(new ConstantExpression(fieldName), parameter.initialExpression))
            else if (getMemberBooleanValue(anno, 'allowDefaultValues', true) && !checkNull(pNode?.initialExpression))
                fieldOf = new MethodCallExpression(baseCodec, 'optionalFieldOf', new ArgumentListExpression(new ConstantExpression(fieldName), pNode.initialExpression))
            else
                fieldOf = new MethodCallExpression(baseCodec, 'fieldOf', new ArgumentListExpression(new ConstantExpression(fieldName)))
        } else
            fieldOf = new MethodCallExpression(baseCodec, 'optionalFieldOf', new ArgumentListExpression(new ConstantExpression(fieldName)))

        ClassNode redirected = makeWithoutCaching(parent.name)
        redirected.redirect = parent

        var forGetterArg = new LambdaExpression(new Parameter[] {new Parameter(redirected, 'it')}, new ReturnStatement(
                new PropertyExpression(new VariableExpression('it', redirected), parameter.name)
        )).tap {
            variableScope = new VariableScope()
        }

        Expression forGetter = new StaticMethodCallExpression(TUPLE_MAP_CODEC, 'forGetter', new ArgumentListExpression(fieldOf, forGetterArg))
        return forGetter
    }

    static boolean checkNull(Expression expr) {
        if (expr === null) return true
        if (expr instanceof ConstantExpression) {
            return expr.value == null
        }
        return false
    }

    static ClassNode unresolveOptional(ClassNode toResolve) {
        if (toResolve == OPTIONAL) {
            if (!toResolve.usingGenerics) {
                throw new RuntimeException('Constructor parameters and their matching fields in codec-serializable classes may not use a raw Optional')
            }
            return toResolve.genericsTypes[0].type
        }
        return toResolve
    }

    static boolean isOptional(ClassNode node) {
        return node == OPTIONAL
    }

    Expression getMemberClosureLambdaExpressionValue(AnnotationNode node, String name) {
        ClassNode value = getMemberClassValue(node, name)
        if (value !== null)
            return null
        final Expression member = node.getMember(name)
        if (member instanceof ClosureExpression)
            if (member.variableScope == null)
                member.variableScope = new VariableScope()
            return member
    }

    static List<WithCodecPath> getMemberCodecPath(AnnotationNode anno, String name) {
        Expression expr = anno.getMember(name)
        if (expr == null) {
            return []
        }
        if (expr instanceof ListExpression) {
            final ListExpression listExpression = (ListExpression) expr
            List<WithCodecPath> list = new ArrayList<>()
            for (Expression itemExpr : listExpression.getExpressions()) {
                if (itemExpr instanceof ConstantExpression) {
                    WithCodecPath value = parseSingleExpr(itemExpr)
                    if (value != null) list.add(value)
                }
            }
            return list
        }
        WithCodecPath single = parseSingleExpr(expr)
        return single==null?([]):([single])
    }

    static WithCodecPath parseSingleExpr(Expression itemExpr) {
        if (itemExpr instanceof VariableExpression) {
            return WithCodecPath.valueOf(itemExpr.text)
        } else if (itemExpr instanceof PropertyExpression) {
            return WithCodecPath.valueOf(itemExpr.propertyAsString)
        }
        return null
    }

    static List<Integer> getMemberCodecTarget(AnnotationNode anno, String name) {
        Expression expr = anno.getMember(name)
        if (expr == null) {
            return []
        }
        if (expr instanceof ListExpression) {
            final ListExpression listExpression = (ListExpression) expr
            List<Integer> list = new ArrayList<>()
            for (Expression itemExpr : listExpression.getExpressions()) {
                if (itemExpr instanceof ConstantExpression) {
                    Integer value = parseSingleTargetExpr(itemExpr)
                    if (value != null) list.add(value)
                }
            }
            return list
        }
        Integer single = parseSingleTargetExpr(expr)
        return single==null?([]):([single])
    }

    static Integer parseSingleTargetExpr(Expression itemExpr) {
        if (itemExpr instanceof ConstantExpression) {
            try {
                return itemExpr.getValue() as Integer
            } catch (GroovyCastException ignored) {}
        }
        return null
    }

    Expression getCodecFromType(ClassNode clazz, List<AnnotationNode> context, List<WithCodecPath> path, List<Integer> target) {
        List<AnnotationNode> withTypes = context.findAll {it.getClassNode() == WITH_TYPE }
                .findAll {
                    var aTarget = getMemberCodecTarget(it, 'target')
                    var aPath = getMemberCodecPath(it, 'path')
                    return (aPath == path && !aPath.empty) || (aTarget == target && !aTarget.isEmpty()) || (aTarget.isEmpty() && aPath.isEmpty() && target.isEmpty()) }
        List<Expression> specifiedClosure = new ArrayList<>(withTypes.collect {
            getMemberClassValue(it, 'value')
        }.findAll {it !== null}.unique().collect {new ConstructorCallExpression(it, new ArgumentListExpression())})
        specifiedClosure.addAll(withTypes.collect {
            getMemberClosureLambdaExpressionValue(it, 'value')
        }.findAll {it !== null}.unique())
        specifiedClosure = specifiedClosure.unique()

        if (specifiedClosure.size() == 1) {
            Expression closure = specifiedClosure[0]
            return new MethodCallExpression(closure, 'call', new ArgumentListExpression())
        } else if (specifiedClosure.size() > 1) {
            throw new RuntimeException("Multiple @WithType annotations with matching targets found: ${withTypes.collect {it.text}}")
        }
        if (clazz == ClassHelper.boolean_TYPE || clazz == ClassHelper.Boolean_TYPE)
            return GeneralUtils.attrX(new ClassExpression(CODEC_NODE), GeneralUtils.constX('BOOL'))
        if (clazz == ClassHelper.short_TYPE || clazz == ClassHelper.Short_TYPE)
            return GeneralUtils.attrX(new ClassExpression(CODEC_NODE), GeneralUtils.constX('SHORT'))
        if (clazz == ClassHelper.byte_TYPE || clazz == ClassHelper.Byte_TYPE)
            return GeneralUtils.attrX(new ClassExpression(CODEC_NODE), GeneralUtils.constX('BYTE'))
        if (clazz == ClassHelper.int_TYPE || clazz == ClassHelper.Integer_TYPE)
            return GeneralUtils.attrX(new ClassExpression(CODEC_NODE), GeneralUtils.constX('INT'))
        if (clazz == ClassHelper.long_TYPE || clazz == ClassHelper.Long_TYPE)
            return GeneralUtils.attrX(new ClassExpression(CODEC_NODE), GeneralUtils.constX('LONG'))
        if (clazz == ClassHelper.float_TYPE || clazz == ClassHelper.Float_TYPE)
            return GeneralUtils.attrX(new ClassExpression(CODEC_NODE), GeneralUtils.constX('FLOAT'))
        if (clazz == ClassHelper.double_TYPE || clazz == ClassHelper.Double_TYPE)
            return GeneralUtils.attrX(new ClassExpression(CODEC_NODE), GeneralUtils.constX('DOUBLE'))
        if (clazz == ClassHelper.STRING_TYPE)
            return GeneralUtils.attrX(new ClassExpression(CODEC_NODE), GeneralUtils.constX('STRING'))
        if (clazz == BYTE_BUFFER)
            return GeneralUtils.attrX(new ClassExpression(CODEC_NODE), GeneralUtils.constX('BYTE_BUFFER'))
        if (clazz == INT_STREAM)
            return GeneralUtils.attrX(new ClassExpression(CODEC_NODE), GeneralUtils.constX('INT_STREAM'))
        if (clazz == LONG_STREAM)
            return GeneralUtils.attrX(new ClassExpression(CODEC_NODE), GeneralUtils.constX('LONG_STREAM'))
        if (clazz == ClassHelper.LIST_TYPE) {
            if (!clazz.usingGenerics) {
                throw new RuntimeException('Constructor parameters and their matching fields in codec-serializable classes may not use a raw List')
            }
            ClassNode child = clazz.genericsTypes[0].type
            Expression childExpression = getCodecFromType(child,context,path+[WithCodecPath.LIST],target+[0])
            return new MethodCallExpression(childExpression, 'commentFirstListOf', new ArgumentListExpression())
        }
        if (clazz == ClassHelper.MAP_TYPE) {
            if (!clazz.usingGenerics) {
                throw new RuntimeException('Constructor parameters and their matching fields in codec-serializable classes may not use a raw Map')
            }
            ClassNode key = clazz.genericsTypes[0].type
            ClassNode value = clazz.genericsTypes[1].type
            Expression keyExpression = getCodecFromType(key,context,path+[WithCodecPath.MAP_KEY],target+[0])
            Expression valueExpression = getCodecFromType(value,context,path+[WithCodecPath.MAP_VAL],target+[1])
            return new StaticMethodCallExpression(CODEC_NODE, 'unboundedMap', new ArgumentListExpression(
                    keyExpression, valueExpression
            ))
        }
        if (clazz == PAIR) {
            if (!clazz.usingGenerics) {
                throw new RuntimeException('Constructor parameters and their matching fields in codec-serializable classes may not use a raw Pair')
            }
            ClassNode left = clazz.genericsTypes[0].type
            ClassNode right = clazz.genericsTypes[1].type
            Expression leftExpression = getCodecFromType(left,context,path+[WithCodecPath.PAIR_FIRST],target+[0])
            Expression rightExpression = getCodecFromType(right,context,path+[WithCodecPath.PAIR_SECOND],target+[1])
            return new StaticMethodCallExpression(CODEC_NODE, 'pair', new ArgumentListExpression(
                    leftExpression, rightExpression
            ))
        }
        if (clazz == EITHER) {
            if (!clazz.usingGenerics) {
                throw new RuntimeException('Constructor parameters and their matching fields in codec-serializable classes may not use a raw Pair')
            }
            ClassNode left = clazz.genericsTypes[0].type
            ClassNode right = clazz.genericsTypes[1].type
            Expression leftExpression = getCodecFromType(left,context,path+[WithCodecPath.EITHER_LEFT],target+[0])
            Expression rightExpression = getCodecFromType(right,context,path+[WithCodecPath.EITHER_RIGHT],target+[1])
            return new StaticMethodCallExpression(CODEC_NODE, 'either', new ArgumentListExpression(
                    leftExpression, rightExpression
            ))
        }
        if (StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(clazz, STRING_REPRESENTABLE) && StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(clazz, ClassHelper.Enum_Type)) {
            return new StaticMethodCallExpression(STRING_REPRESENTABLE, 'fromEnum',
                    new MethodReferenceExpression(new ClassExpression(clazz), new ConstantExpression('values')))
        }
        List<String> givenFields = clazz.annotations.findAll {it.getClassNode() == MY_TYPE }.collect {getMemberStringValue(it, 'property', DEFAULT_CODEC_PROPERTY)}
        givenFields.addAll clazz.annotations.findAll { it.getClassNode() == EXPOSES_TYPE }.collect { getMemberStringValue(it, 'value', '') }.findAll {it != ''}
        givenFields.addAll(getExposes(clazz))

        if (givenFields.size() >= 1) {
            return new PropertyExpression(new ClassExpression(clazz), givenFields[0]).tap {
                setStatic(true)
            }
        }
        List<FieldNode> codecFields = clazz.fields.findAll {
            if (!it.static)
                return false
            if (it.type != CODEC_NODE)
                return false
            if (!it.type.usingGenerics)
                return false
            return it.type.genericsTypes[0].type == clazz
        }
        if (codecFields.size() == 1) {
            return GeneralUtils.attrX(new ClassExpression(clazz), GeneralUtils.constX(codecFields[0].name))
        }

        List<AnnotationNode> annotationFactories = clazz.annotations.findAll { it.getClassNode() == EXPOSES_FACTORY_TYPE }
        annotationFactories.addAll(getFactories(clazz))
        if (annotationFactories.size() >= 1) {
            var factory = annotationFactories[0]
            var factoryName = getMemberStringValue(factory, 'value', '')
            if (factoryName.empty) {
                throw new RuntimeException("The @ExposesFactory annotation on ${clazz.name} must have a value.")
            }
            List<Expression> subExpressions = new ArrayList<>()
            if (clazz.usingGenerics) {
                var parameters = clazz.genericsTypes.collect { it.type }
                for (int i = 0; i < parameters.size(); i++) {
                    subExpressions.add(getCodecFromType(parameters[i], context, path + [WithCodecPath.FACTORY_PARAMETER], target + [i]))
                }
            }
            return GeneralUtils.callX(new ClassExpression(clazz), factoryName, GeneralUtils.args(subExpressions))
        }

        throw new RuntimeException("A codec cannot be found for type ${clazz.toString(false)}.")
    }

    static final ClassNode EXPOSE_FACTORY_TYPE = makeWithoutCaching(ExposeCodecFactory)
    static List<AnnotationNode> getFactories(ClassNode classNode) {
        List<AnnotationNode> factories = new ArrayList<>()
        for (MethodNode method : classNode.methods) {
            factories.addAll(method.annotations.findAll {
                it.getClassNode() == EXPOSE_FACTORY_TYPE
            }.collect {
                new AnnotationNode(EXPOSES_FACTORY_TYPE).tap {
                    addMember('value', new ConstantExpression(method.name))
                    addMember('parameters', new ConstantExpression(method.parameters.length))
                }
            })
        }
        return factories
    }
    
    static final ClassNode EXPOSE_CODEC_TYPE = makeWithoutCaching(ExposeCodec)
    static List<String> getExposes(ClassNode classNode) {
        List<String> exposes = new ArrayList<>()
        for (PropertyNode property : classNode.properties) {
            if (property.annotations.any { it.getClassNode() == EXPOSE_CODEC_TYPE }) {
                exposes.add(property.name)
            }
        }
        return exposes
    }

    @Override
    int priority() {
        return -1
    }
}
