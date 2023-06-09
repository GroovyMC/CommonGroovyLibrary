/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.cgl.api.codec.comments

import groovy.transform.CompileStatic
import org.jetbrains.annotations.Nullable

/**
 * An object that provides comments for a {@link org.groovymc.cgl.api.codec.comments.MapCommentSpec}.
 */
@CompileStatic
interface CommentSpec {
    /**
     * Provides a {@link java.lang.String} comment based on the provided key.
     * @param mapKey The key to look up a comment for.
     * @return The comment associated with that key, or null if no such comment exists.
     */
    @Nullable String getComment(String mapKey)
}
