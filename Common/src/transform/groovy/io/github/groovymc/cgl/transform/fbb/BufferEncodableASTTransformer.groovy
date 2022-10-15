/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.transform.fbb


import groovy.transform.CompileStatic
import io.github.groovymc.cgl.transform.TransformUtils
import net.minecraft.network.FriendlyByteBuf
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.tools.GeneralUtils
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.TransformWithPriority
import org.objectweb.asm.Opcodes

@CompileStatic
@GroovyASTTransformation
final class BufferEncodableASTTransformer extends AbstractASTTransformation implements TransformWithPriority {

    public static final ClassNode BUF_TYPE = ClassHelper.make(FriendlyByteBuf)
    public static final ClassNode WRITER_TYPE = ClassHelper.make(FriendlyByteBuf.Writer)
    public static final ClassNode READER_TYPE = ClassHelper.make(FriendlyByteBuf.Reader)
    public static final ClassNode LIST = ClassHelper.make(List)

    private static final Map<ClassNode, String> PRIMITIVE_METHOD_SUFFIX = new HashMap<ClassNode, String>().tap {
        it[ClassHelper.int_TYPE] = 'Int'
        it[ClassHelper.Integer_TYPE] = 'Int'

        it[ClassHelper.long_TYPE] = 'Long'
        it[ClassHelper.Long_TYPE] = 'Long'

        it[ClassHelper.double_TYPE] = 'Double'
        it[ClassHelper.Double_TYPE] = 'Double'

        it[ClassHelper.float_TYPE] = 'Float'
        it[ClassHelper.Float_TYPE] = 'Float'

        it[ClassHelper.byte_TYPE] = 'Byte'
        it[ClassHelper.Byte_TYPE] = 'Byte'

        it[ClassHelper.short_TYPE] = 'Short'
        it[ClassHelper.Short_TYPE] = 'Short'

        it[ClassHelper.boolean_TYPE] = 'Boolean'
        it[ClassHelper.Boolean_TYPE] = 'Boolean'

        it[ClassHelper.STRING_TYPE] = 'Utf'
    }

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source)
        final var clazz = nodes[1] as ClassNode

        TransformUtils.addMethod(
                targetClassNode: clazz,
                methodName: 'encode'
        ).tap {
            final param = new Parameter(BUF_TYPE, 'buffer')
            it.parameters = new Parameter[]{param}
            final var buf = GeneralUtils.localVarX(param.name, BUF_TYPE)
            final List<Statement> stmts = []
            clazz.properties.each {
                stmts.add(createWriteStatement(buf, it))
            }
            stmts.remove(null)
            setCode(new BlockStatement(stmts, new VariableScope()))
        }

        TransformUtils.addMethod(
                targetClassNode: clazz,
                methodName: 'decode',
                returnType: clazz.properties[0].declaringClass,
                modifiers: Opcodes.ACC_PUBLIC
        ).tap {
            final param = new Parameter(BUF_TYPE, 'buffer')
             it.parameters = new Parameter[]{param}
            final List<Expression> stmts = []
            final var expr = GeneralUtils.varX(param)
            clazz.properties.each {
                stmts.add(createReadStatement(expr, it))
            }
            stmts.remove(null)
            setCode(GeneralUtils.returnS(GeneralUtils.ctorX(clazz.properties[0].declaringClass, GeneralUtils.args(stmts))))
        }
    }

    static Statement createWriteStatement(VariableExpression buffer, PropertyNode property) {
        final var name = getWriteMethodName(property.type)
        final var propX = GeneralUtils.propX(GeneralUtils.varX('this'), property.name)
        Expression expression = null
        if (name !== null) {
            expression = GeneralUtils.callX(buffer, name, propX)
        } else if (property.type == LIST) {
            final var listType = property.type.genericsTypes[0].type
            final lambdaMethod = TransformUtils.addMethod(
                    targetClassNode: property.declaringClass,
                    methodName: "bufferencodable\$write\$lambda${property.name}",
                    parameters: new Parameter[] {
                        new Parameter(BUF_TYPE, 'buf'),
                        new Parameter(listType, 'val')
                    },
                    modifiers: Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC,
                    code: GeneralUtils.stmt(GeneralUtils.callX(GeneralUtils.localVarX('buf', BUF_TYPE), getWriteMethodName(listType), GeneralUtils.localVarX('val', listType)))
            )
            expression = GeneralUtils.callX(
                    buffer, 'writeCollection', GeneralUtils.args(
                        propX, GeneralUtils.bytecodeX(WRITER_TYPE) {
                            TransformUtils.indy(
                                    it, WRITER_TYPE, lambdaMethod, property.declaringClass
                            )
                        }
                )
            )
        }
        if (expression !== null) {
            return GeneralUtils.stmt(expression)
        }
        return null
    }

    static String getWriteMethodName(ClassNode type) {
        return PRIMITIVE_METHOD_SUFFIX[type]?.with { 'write' + it }
    }

    static Expression createReadStatement(VariableExpression buffer, PropertyNode property) {
        final var name = getReadMethodName(property.type)
        if (name !== null) {
            return GeneralUtils.callX(buffer, name)
        } else if (property.type == LIST) {
            final var listType = property.type.genericsTypes[0].type
            final lambdaMethod = TransformUtils.addMethod(
                    targetClassNode: property.declaringClass,
                    methodName: "bufferencodable\$read\$lambda${property.name}",
                    parameters: new Parameter[] {
                            new Parameter(BUF_TYPE, 'buf')
                    },
                    returnType: listType,
                    modifiers: Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC,
                    code: GeneralUtils.returnS(GeneralUtils.callX(GeneralUtils.localVarX('buf', BUF_TYPE), getReadMethodName(listType)))
            )
            return GeneralUtils.callX(
                    buffer, 'readList', GeneralUtils.bytecodeX(READER_TYPE) {
                        TransformUtils.indy(
                                it, READER_TYPE, lambdaMethod, property.declaringClass
                        )
                    }
            )
        }
        return null
    }

    static String getReadMethodName(ClassNode type) {
        return PRIMITIVE_METHOD_SUFFIX[type]?.with { 'read' + it }
    }

    @Override
    int priority() {
        return -1
    }
}
