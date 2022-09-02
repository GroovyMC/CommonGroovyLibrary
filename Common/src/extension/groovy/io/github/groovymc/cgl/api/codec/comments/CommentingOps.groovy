/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.codec.comments

import com.mojang.serialization.DynamicOps
import groovy.transform.CompileStatic

@CompileStatic
interface CommentingOps<T> extends DynamicOps<T> {
    T finalize(T input, CommentSpec comments)

    DynamicOps<T> withoutComments()
}
