/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.cgl.api.codec.comments

import com.mojang.serialization.DynamicOps
import groovy.transform.CompileStatic

/**
 * A {@link com.mojang.serialization.DynamicOps} that can encode comments as well as data.
 */
@CompileStatic
interface CommentingOps<T> extends DynamicOps<T> {
    /**
     * Finish encoding a provided object by adding comments to it based on the provided {@link CommentSpec}.
     * @param input The object to add comments to. May not be mutated by the ops.
     * @param comments The comments to add to the object.
     * @return A copy of the input, with comments attached based on the provided {@link CommentSpec}.
     */
    T finalize(T input, CommentSpec comments)

    /**
     * Provide a version of this {@link com.mojang.serialization.DynamicOps} that does not attach comments.
     */
    DynamicOps<T> withoutComments()
}
