/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.codec.comments

import groovy.transform.CompileStatic
import org.jetbrains.annotations.Nullable

@CompileStatic
interface CommentSpec {
    @Nullable String getComment(String mapKey)
}