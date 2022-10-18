/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.quilt.transform.environment

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.environment.Loader
import io.github.groovymc.cgl.api.environment.Side
import io.github.groovymc.cgl.transform.environment.ExistsOnProcessor
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.jetbrains.annotations.ApiStatus
import org.quiltmc.loader.api.minecraft.ClientOnly
import org.quiltmc.loader.api.minecraft.DedicatedServerOnly

import static org.codehaus.groovy.ast.ClassHelper.makeWithoutCaching

@CompileStatic
@ApiStatus.Internal
class ExistsOnProcessorImpl implements ExistsOnProcessor {
    static final ClassNode CLIENT_ONLY = makeWithoutCaching(ClientOnly)
    static final ClassNode SERVER_ONLY = makeWithoutCaching(DedicatedServerOnly)

    @Override
    void process(AnnotatedNode node, Side side, List<Loader> loaders) {
        if (loaders.contains(Loader.QUILT)) {
            switch (side) {
                case Side.CLIENT:
                    node.addAnnotation(new AnnotationNode(CLIENT_ONLY))
                    break
                case Side.SERVER:
                    node.addAnnotation(new AnnotationNode(SERVER_ONLY))
                    break
            }
        }
    }
}
