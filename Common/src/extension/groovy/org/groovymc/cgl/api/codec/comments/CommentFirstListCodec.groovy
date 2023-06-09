/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.cgl.api.codec.comments

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.ListBuilder

/**
 * A {@link com.mojang.serialization.Codec} representing a list of objects, where the first element will have its
 * comments attached, and the others will be encoded without comments.
 */
final class CommentFirstListCodec<O> implements Codec<List<O>> {
    private final Codec<O> elementCodec
    private final Codec<List<O>> decodeDelegate

    /**
     * Construct a new first-element-commented list code from the provided single element codec.
     * @param elementCodec A codec representing a single element. Should contain any comment behavior that will be
     * applied during encoding.
     */
    CommentFirstListCodec(Codec<O> elementCodec) {
        this.elementCodec = elementCodec
        this.decodeDelegate = elementCodec.listOf()
    }

    @Override
    <T> DataResult<Pair<List<O>, T>> decode(DynamicOps<T> ops, T input) {
        return decodeDelegate.decode(ops, input)
    }

    @Override
    <T> DataResult<T> encode(List<O> input, DynamicOps<T> ops, T prefix) {
        final ListBuilder<T> builder = ops.listBuilder()
        DynamicOps<T> rest = ops instanceof CommentingOps ? ops.withoutComments() : ops
        boolean isFirst = true
        for (O o : input) {
            if (isFirst) {
                builder.add(elementCodec.encodeStart(ops, o))
                isFirst = false
                continue
            }
            builder.add(elementCodec.<T>encodeStart(rest, o))
        }
        return builder.build(prefix)
    }
}
