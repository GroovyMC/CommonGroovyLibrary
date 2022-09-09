/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.extension

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import groovy.transform.AutoFinal
import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.codec.CommentedFieldCodec
import io.github.groovymc.cgl.api.codec.comments.CommentFirstListCodec
import io.github.groovymc.cgl.api.codec.comments.CommentSpec
import io.github.groovymc.cgl.api.codec.comments.CommentedCodec
import io.github.groovymc.cgl.api.codec.comments.MapCommentSpec

/**
 * Extensions for {@linkplain Codec Codecs}.
 * @author CommonGroovyLibrary
 */
@CompileStatic
@AutoFinal
class CodecExtensions {
    @Deprecated(forRemoval = true, since = '0.1.2')
    static <O> Codec<O> comment(Codec<O> codec, CommentSpec spec) {
        return new CommentedCodec<>(codec, spec)
    }

    static <O> Codec<List<O>> commentFirstListOf(Codec<O> codec) {
        return new CommentFirstListCodec<>(codec)
    }

    static <O> MapCodec<O> commentedFieldOf(Codec<O> codec, String field, String comment) {
        return new CommentedFieldCodec<O>(
                codec.fieldOf(field),
                MapCommentSpec.of([field:comment])
        )
    }

    static <O> MapCodec<O> commentedOptionalFieldOf(Codec<O> codec, String field, O defaultValue, String comment) {
        return new CommentedFieldCodec<O>(
                codec.optionalFieldOf(field, defaultValue),
                MapCommentSpec.of([field:comment])
        )
    }

    static <O> MapCodec<Optional<O>> commentedOptionalFieldOf(Codec<O> codec, String field, String comment) {
        return new CommentedFieldCodec<Optional<O>>(
                codec.optionalFieldOf(field),
                MapCommentSpec.of([field:comment])
        )
    }
}
