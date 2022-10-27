/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.forge.transform.environment

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.environment.Loader
import io.github.groovymc.cgl.api.environment.Platform
import io.github.groovymc.cgl.api.environment.Side
import io.github.groovymc.cgl.transform.environment.ExistsOnProcessor
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.control.CompilePhase
import org.jetbrains.annotations.ApiStatus

import static org.codehaus.groovy.ast.ClassHelper.makeWithoutCaching

@CompileStatic
@ApiStatus.Internal
class ExistsOnProcessorImpl implements ExistsOnProcessor {
    static final ClassNode ONLY_IN = makeWithoutCaching(OnlyIn)
    static final ClassNode DIST = makeWithoutCaching(Dist)

    @Override
    void process(AnnotatedNode node, List<Platform> platforms, ModuleNode ast, CompilePhase phase) {
        List<Side> sides = platforms.findAll {it.loader === Loader.FORGE}.collect {it.side}.unique()
        if (sides.size() === 1) {
            if (phase === CompilePhase.CANONICALIZATION) {
                Side side = sides[0]
                switch (side) {
                    case Side.CLIENT:
                        node.addAnnotation(new AnnotationNode(ONLY_IN).tap {
                            it.addMember('value', new PropertyExpression(
                                    new ClassExpression(DIST),
                                    new ConstantExpression(Dist.CLIENT)))
                        })
                        break
                    case Side.SERVER:
                        node.addAnnotation(new AnnotationNode(ONLY_IN).tap {
                            it.addMember('value', new PropertyExpression(
                                    new ClassExpression(DIST),
                                    new ConstantExpression(Dist.DEDICATED_SERVER)))
                        })
                        break
                }
            }
        } else if (sides.size() === 0) {
            if (phase === CompilePhase.INSTRUCTION_SELECTION) {
                if (node instanceof MethodNode) {
                    node.declaringClass.removeMethod(node)
                } else if (node instanceof FieldNode) {
                    node.declaringClass.removeField(node.name)
                } else if (node instanceof ConstructorNode) {
                    node.declaringClass.removeConstructor(node)
                } else if (node instanceof ClassNode) {
                    ast.classes.remove(node)
                }
            }
        }
    }
}
