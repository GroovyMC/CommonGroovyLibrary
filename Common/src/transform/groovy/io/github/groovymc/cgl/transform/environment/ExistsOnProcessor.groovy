/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.transform.environment

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.environment.Loader
import io.github.groovymc.cgl.api.environment.Side
import org.codehaus.groovy.ast.AnnotatedNode
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
@CompileStatic
interface ExistsOnProcessor {
    void process(AnnotatedNode node, Side side, List<Loader> loaders)
}