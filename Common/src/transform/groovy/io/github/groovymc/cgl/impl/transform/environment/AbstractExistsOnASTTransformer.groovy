/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.impl.transform.environment

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.environment.Platform
import io.github.groovymc.cgl.api.transform.environment.ExistsOn
import io.github.groovymc.cgl.impl.transform.TransformUtils
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.jetbrains.annotations.ApiStatus

import static org.codehaus.groovy.ast.ClassHelper.makeWithoutCaching

@CompileStatic
@ApiStatus.Internal
abstract class AbstractExistsOnASTTransformer extends AbstractASTTransformation {
    static final ClassNode MY_TYPE = makeWithoutCaching(ExistsOn)

    static final ExistsOnProcessor PROCESSOR = ServiceLoader.load(ExistsOnProcessor, AbstractExistsOnASTTransformer.class.classLoader)
            .findFirst()
            .orElse(null)

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        init(nodes, source)
        AnnotatedNode parent = (AnnotatedNode) nodes[1]
        AnnotationNode anno = (AnnotationNode) nodes[0]
        if (MY_TYPE != anno.getClassNode()) return

        //noinspection UnnecessaryQualifiedReference
        List<Platform> platforms = TransformUtils.<Platform>getMemberValues(anno, 'value', Platform::valueOf)

        PROCESSOR?.process(parent, platforms, source.AST, phase)
    }

    abstract CompilePhase getPhase();
}
