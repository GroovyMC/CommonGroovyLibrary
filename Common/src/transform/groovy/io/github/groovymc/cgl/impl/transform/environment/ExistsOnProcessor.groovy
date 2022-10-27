/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.impl.transform.environment

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.environment.Platform
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.control.CompilePhase
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
@CompileStatic
interface ExistsOnProcessor {
    void process(AnnotatedNode node, List<Platform> platforms, ModuleNode ast, CompilePhase phase)
}