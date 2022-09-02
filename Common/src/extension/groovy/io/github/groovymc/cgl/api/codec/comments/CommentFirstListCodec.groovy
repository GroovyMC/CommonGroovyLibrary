package io.github.groovymc.cgl.api.codec.comments

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.ListBuilder

final class CommentFirstListCodec<O> implements Codec<List<O>> {
    private final Codec<O> elementCodec
    private final Codec<List<O>> decodeDelegate

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
