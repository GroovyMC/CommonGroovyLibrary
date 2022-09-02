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

@CompileStatic
final class CommentedCodec<O> implements Codec<O> {
    private Codec<O> delegate
    private CommentSpec spec

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