/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.codec.comments

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import groovy.transform.CompileStatic

/**
 * A {@link com.mojang.serialization.Codec} with comments attached. The delegate codec is assumed to be encode to a
 * map-like structure; if this is not the case, comments will not be attached when encoding with a comment-supporting
 * {@link com.mojang.serialization.DynamicOps}.
 * @deprecated use {@link io.github.groovymc.cgl.api.codec.TupleMapCodec} instead.
 */
@Deprecated(forRemoval = true, since = '0.1.2')
@CompileStatic
final class CommentedCodec<O> implements Codec<O> {
    final Codec<O> delegate
    final CommentSpec spec

    /**
     * Construct a new commented codec using the provided delegate codec and comment specification
     * @param delegate The codec to delegate encoding and decoding logic to.
     * @param spec The comment specification for the constructed codec.
     */
    CommentedCodec(Codec<O> delegate, CommentSpec spec) {
        this.delegate = delegate
        this.spec = spec
    }

    @Override
    <T> DataResult<Pair<O, T>> decode(DynamicOps<T> ops, T input) {
        return delegate.decode(ops, input)
    }

    @Override
    <T> DataResult<T> encode(O input, DynamicOps<T> ops, T prefix) {
        if (ops instanceof CommentingOps<T>) {
            return delegate.encode(input, ops, prefix).map {
                ops.finalize(it, spec)
            }
        }
        return delegate.encode(input, ops, prefix)
    }
}