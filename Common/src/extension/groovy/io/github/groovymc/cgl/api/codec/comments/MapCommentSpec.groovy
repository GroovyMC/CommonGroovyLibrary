package io.github.groovymc.cgl.api.codec.comments

import groovy.transform.CompileStatic

@CompileStatic
class MapCommentSpec implements CommentSpec {
    Map<String, String> values

    private MapCommentSpec(Map<String, String> values) {
        this.values = values
    }

    static MapCommentSpec of(Map<String, String> values) {
        return new MapCommentSpec(values)
    }

    @Override
    String getComment(String mapKey) {
        return values.get(mapKey)
    }
}
