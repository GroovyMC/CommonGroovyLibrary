/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.transform.codec

import groovy.transform.CompileStatic
import org.apache.groovy.util.BeanUtils
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport
import org.jetbrains.annotations.ApiStatus

import static org.codehaus.groovy.ast.ClassHelper.makeWithoutCaching

@CompileStatic
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
@ApiStatus.Internal
class ExposeCodecTransformation extends AbstractASTTransformation {

    static final ClassNode MY_TYPE = makeWithoutCaching(io.github.lukebemish.groovyduvet.wrapper.minecraft.api.codec.ExposeCodec)
    static final ClassNode TARGET_TYPE = makeWithoutCaching(io.github.lukebemish.groovyduvet.wrapper.minecraft.api.codec.ExposesCodec)
    static final ClassNode CODEC_NODE = makeWithoutCaching('com.mojang.serialization.Codec')

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        init(nodes, source)
        AnnotatedNode parent = (AnnotatedNode) nodes[1]
        AnnotationNode anno = (AnnotationNode) nodes[0]
        if (MY_TYPE != anno.getClassNode()) return

        if (parent instanceof FieldNode) {
            if (!parent.static) {
                throw new RuntimeException('ExposeCodec may only expose static properties!')
            }
            ClassNode clazz = parent.declaringClass
            ClassNode type = parent.type
            String name = parent.name
            doApply(clazz, type, name)
        } else if (parent instanceof MethodNode) {
            if (!parent.static) {
                throw new RuntimeException('ExposeCodec may only expose static properties!')
            }
            if (parent.parameters.size() != 0) {
                throw new RuntimeException("ExposeCodec can only be used to expose fields or getters from within a class. Currently applied to the method $parent.name, which takes ${parent.parameters.size()} arguments.")
            }
            ClassNode clazz = parent.declaringClass
            ClassNode type = parent.returnType
            if (! parent.name.startsWith('get')) {
                throw new RuntimeException("ExposeCodec can only be used to expose fields or getters from within a class. Currently applied to the method ${parent.name}.")
            }
            String name = BeanUtils.decapitalize(parent.name.substring(3))
            doApply(clazz, type, name)
        }
    }

    static void doApply(ClassNode parent, ClassNode type, String name) {
        if (!StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(type, CODEC_NODE)) {
            throw new RuntimeException("ExposeCodec used in ${parent.name} must annotate either a field of type Codec<${parent.nameWithoutPackage}> or a getter that return Codec<${parent.nameWithoutPackage}>. Current detected type is ${type.toString(false)}")
        }
        if (parent.annotations*.classNode.find {it == TARGET_TYPE}) {
            throw new RuntimeException("Cannot use ExposeCodec to expose a property when the parent class already has a codec exposed!")
        }
        parent.addAnnotation(new AnnotationNode(TARGET_TYPE).tap {
            addMember('value', new ConstantExpression(name))
        })
    }
}
