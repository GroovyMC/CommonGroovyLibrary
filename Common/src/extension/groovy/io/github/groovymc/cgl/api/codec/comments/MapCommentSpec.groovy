/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.codec.comments

import groovy.transform.CompileStatic

/**
 * A {@link CommentSpec} backed by a {@link Map}.
 */
@CompileStatic
class MapCommentSpec implements CommentSpec {
    Map<String, String> values

    private MapCommentSpec(Map<String, String> values) {
        this.values = values
    }

    /**
     * Build a new {@link CommentSpec} backed by the provided map. A direct reference to the map is made, so that if the
     * map is later mutated the spec will be as well.
     */
    static MapCommentSpec of(Map<String, String> values) {
        return new MapCommentSpec(values)
    }

    @Override
    String getComment(String mapKey) {
        return values.get(mapKey)
    }

    @Override
    String toString() {
        return "MapCommentSpec[${String.join('; ',values.collect {key, val -> "$key: '$val'"})}]"
    }
}
