/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.codec.comments

import com.mojang.serialization.*
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor

import java.util.function.UnaryOperator
import java.util.stream.Stream
/**
 * A {@link MapCodec} that wraps another {@link MapCodec}, providing a comment during encoding. Should work most places
 * that the original MapCodec would.
 * @param <O>
 */
@CompileStatic
@TupleConstructor
class CommentedFieldMapCodec<O> extends MapCodec<O> {
    final MapCodec<O> delegate
    final CommentSpec comments

    @Override
    def <T> Stream<T> keys(DynamicOps<T> ops) {
        return delegate.keys(ops)
    }

    @Override
    def <T> DataResult<O> decode(DynamicOps<T> ops, MapLike<T> input) {
        return delegate.decode(ops, input)
    }

    @Override
    def <T> RecordBuilder<T> encode(O input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        prefix = delegate.encode(input, ops, prefix)

        return new RecordBuilder<T>() {
            RecordBuilder<T> builder = prefix
            @Override
            DynamicOps<T> ops() {
                return builder.ops()
            }

            @Override
            RecordBuilder<T> add(T key, T value) {
                builder = builder.add(key, value)
                return this
            }

            @Override
            RecordBuilder<T> add(T key, DataResult<T> value) {
                builder = builder.add(key, value)
                return this
            }

            @Override
            RecordBuilder<T> add(DataResult<T> key, DataResult<T> value) {
                builder = builder.add(key, value)
                return this
            }

            @Override
            RecordBuilder<T> withErrorsFrom(DataResult<?> result) {
                builder = builder.withErrorsFrom(result)
                return this
            }

            @Override
            RecordBuilder<T> setLifecycle(Lifecycle lifecycle) {
                builder = builder.setLifecycle(lifecycle)
                return this
            }

            @Override
            RecordBuilder<T> mapError(UnaryOperator<String> onError) {
                builder = builder.mapError(onError)
                return this
            }

            @Override
            DataResult<T> build(T buildPrefix) {
                DataResult<T> built = builder.build(buildPrefix)
                if (this.ops() instanceof CommentingOps<T>) {
                    CommentingOps<T> commentingOps = (CommentingOps<T>) this.ops()
                    return built.map { commentingOps.finalize(it, comments) }
                }
                return built
            }
        }
    }
}
