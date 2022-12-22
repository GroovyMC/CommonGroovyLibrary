/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.extension

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import groovy.transform.AutoFinal
import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.codec.comments.CommentFirstListCodec
import io.github.groovymc.cgl.api.codec.comments.CommentedFieldMapCodec
import io.github.groovymc.cgl.api.codec.comments.MapCommentSpec
/**
 * Extensions for {@linkplain Codec Codecs}.
 * @author CommonGroovyLibrary
 */
@CompileStatic
@AutoFinal
class CodecExtensions {
    /**
     * Similar to {@link Codec#listOf}, except that if the provided codec is commented, the output list will comment
     * only the first member of the encoded list during encoding.
     * @param codec
     * @return
     */
    static <O> Codec<List<O>> commentFirstListOf(Codec<O> codec) {
        return new CommentFirstListCodec<>(codec)
    }

    /**
     * Creates a {@link CommentedFieldMapCodec} based on the provided codec, similar to {@Codec#fieldOf}.
     * @param field The name of the field.
     * @param comment The comment to attach to the field.
     * @return A {@link MapCodec} that will attach that comment during encoding.
     */
    static <O> MapCodec<O> commentedFieldOf(Codec<O> codec, String field, String comment) {
        return new CommentedFieldMapCodec<O>(
                codec.fieldOf(field),
                MapCommentSpec.of([field:comment])
        )
    }

    /**
     * Creates a {@link CommentedFieldMapCodec} based on the provided codec, similar to {@Codec#optionalFieldOf}.
     * @param field The name of the field.
     * @param defaultValue The default value for the field if no value is found while decoding.
     * @param comment The comment to attach to the field.
     * @return A {@link MapCodec} that will attach that comment during encoding.
     */
    static <O> MapCodec<O> commentedOptionalFieldOf(Codec<O> codec, String field, O defaultValue, String comment) {
        return new CommentedFieldMapCodec<O>(
                codec.optionalFieldOf(field, defaultValue),
                MapCommentSpec.of([field:comment])
        )
    }

    /**
     * Creates a {@link CommentedFieldMapCodec} based on the provided codec, similar to {@Codec#optionalFieldOf}.
     * @param field The name of the optional field.
     * @param comment The comment to attach to the field.
     * @return A {@link MapCodec} that will attach that comment during encoding.
     */
    static <O> MapCodec<Optional<O>> commentedOptionalFieldOf(Codec<O> codec, String field, String comment) {
        return new CommentedFieldMapCodec<Optional<O>>(
                codec.optionalFieldOf(field),
                MapCommentSpec.of([field:comment])
        )
    }

    /**
     * Creates a codec that can process for either a single member of provided codec or a list, always decoding to a
     * list.
     */
    static <O> Codec<List<O>> singleOrListOf(Codec<O> codec) {
        return Codec.either(codec, codec.listOf()).<List<O>>xmap({either ->
            either.<List<O>>map({
                List.of(it)
            },{
                it
            })
        },{
            it.size() == 1 ? Either<O, List<O>>.left(it.get(0)) : Either<O, List<O>>.right(it)
        })
    }
}
