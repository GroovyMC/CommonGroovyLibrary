/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.extension

import com.mojang.serialization.Codec
import io.github.groovymc.cgl.api.codec.comments.CommentFirstListCodec
import io.github.groovymc.cgl.api.codec.comments.CommentSpec
import io.github.groovymc.cgl.api.codec.comments.CommentedCodec

/**
 * Extensions for {@linkplain Codec Codecs}.
 * @author CommonGroovyLibrary
 */
class CodecExtensions {
    static <O> Codec<O> comment(Codec<O> codec, CommentSpec spec) {
        return new CommentedCodec<>(codec, spec)
    }

    static <O> Codec<List<O>> commentFirstListOf(Codec<O> codec) {
        return new CommentFirstListCodec<>(codec)
    }
}
