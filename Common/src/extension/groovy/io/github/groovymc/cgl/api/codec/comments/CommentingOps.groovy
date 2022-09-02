package io.github.groovymc.cgl.api.codec.comments

import com.mojang.serialization.DynamicOps
import groovy.transform.CompileStatic

@CompileStatic
interface CommentingOps<T> extends DynamicOps<T> {
    T finalize(T input, CommentSpec comments)

    DynamicOps<T> withoutComments()
}
