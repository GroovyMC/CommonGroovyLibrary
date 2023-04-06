/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.impl.transform.codec

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.transform.codec.ExposesCodec
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.TransformWithPriority
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport

import static org.codehaus.groovy.ast.ClassHelper.makeWithoutCaching

@CompileStatic
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
class ExposesCodecChecker extends AbstractASTTransformation implements TransformWithPriority {

    static final ClassNode MY_TYPE = makeWithoutCaching(ExposesCodec)

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        init(nodes, source)
        AnnotatedNode parent = (AnnotatedNode) nodes[1]
        AnnotationNode anno = (AnnotationNode) nodes[0]
        if (MY_TYPE != anno.getClassNode()) return

        if (parent instanceof ClassNode) {
            if (parent.genericsTypes.length != 0) {
                throw new RuntimeException('ExposesCodec may only be used on classes with no generics!')
            }

            String value = getMemberStringValue(anno, 'value', '')
            if (value.isEmpty()) {
                throw new RuntimeException('ExposesCodec must have a value!')
            }
            PropertyNode property = parent.properties.find {
                it.name == value && it.static
            }
            if (property === null) {
                throw new RuntimeException("ExposesCodec value $value is not a static property of $parent.name!")
            }

            var parameterizedCodec = makeWithoutCaching('com.mojang.serialization.Codec')
            parameterizedCodec.setGenericsTypes(new GenericsType[]{new GenericsType(parent)})
            if (!StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(property.type, parameterizedCodec)) {
                throw new RuntimeException("ExposesCodec value $value does not expose a properly parameterized codec!")
            }
        }
    }

    @Override
    int priority() {
        return -2
    }
}