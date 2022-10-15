/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.forge.transform.environment

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.transform.environment.ExistsOnProcessor
import io.github.groovymc.cgl.api.environment.Loader
import io.github.groovymc.cgl.api.environment.Side
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.jetbrains.annotations.ApiStatus

import static org.codehaus.groovy.ast.ClassHelper.makeWithoutCaching

@CompileStatic
@ApiStatus.Internal
class ExistsOnProcessorImpl implements ExistsOnProcessor {
    static final ClassNode ONLY_IN = makeWithoutCaching(OnlyIn)
    static final ClassNode DIST = makeWithoutCaching(Dist)

    @Override
    void process(AnnotatedNode node, Side side, List<Loader> loaders) {
        if (loaders.contains(Loader.FORGE)) {
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
    }
}
