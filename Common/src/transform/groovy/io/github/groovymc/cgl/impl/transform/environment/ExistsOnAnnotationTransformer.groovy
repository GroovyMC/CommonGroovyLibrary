/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.impl.transform.environment

import groovy.transform.CompileStatic
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.jetbrains.annotations.ApiStatus

@CompileStatic
@ApiStatus.Internal
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
class ExistsOnAnnotationTransformer extends AbstractExistsOnASTTransformer {
    @Override
    CompilePhase getPhase() {
        return CompilePhase.CANONICALIZATION
    }
}
