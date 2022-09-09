package io.github.groovymc.cgl.api.codec

import com.mojang.serialization.*
import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.codec.comments.CommentSpec
import io.github.groovymc.cgl.api.codec.comments.CommentingOps
import io.github.groovymc.cgl.api.codec.comments.MapCommentSpec

import java.util.concurrent.atomic.AtomicReference
import java.util.function.UnaryOperator
import java.util.stream.Stream

/**
 * A {@link MapCodec} that combines an arbitrary number of {@link TupleMapCodec.GetterMapCodec} representing arguments
 * of an assembler closure, and optionally attaches a {@link CommentSpec} during encoding.
 */
@CompileStatic
class TupleMapCodec<O> extends MapCodec<O> {
    private final GetterMapCodec[] codecs
    private final Closure<O> assembler
    private final CommentSpec spec

    private TupleMapCodec(Closure<O> assembler, CommentSpec spec, GetterMapCodec[] codecs) {
        this.codecs = codecs
        this.assembler = assembler
        this.spec = spec
    }

    /**
     * Combine a {@link MapCodec} and a getter that provides the type encoded by the {@link MapCodec}.
     */
    static <A> GetterMapCodec forGetter(MapCodec<A> codec, Closure<A> getter) {
        return new GetterMapCodec(codec, getter)
    }

    /**
     * Begin a builder using a series of {@link MapCodec} linked to getters.
     * @param assembler The function that will assemble the final object, given a list of arguments
     * @param codecs The combined {@link MapCodec} and getters, in order, to use in the codec.
     * @return A MapCodec combining the provided single-field map codecs using the assembler.
     */
    static <A> TupleMapCodec<A> of(Closure<A> assembler, GetterMapCodec[] codecs) {
        return new TupleMapCodec(assembler, MapCommentSpec.of([:]), codecs)
    }

    /**
     * Create a codec using a series of {@link MapCodec} linked to getters and an assembly closure.
     * @param assembler The function that will assemble the final object, given a list of arguments
     * @param spec The comment specification for the constructed codec.
     * @param codecs The combined {@link MapCodec} and getters, in order, to use in the codec.
     * @return A MapCodec combining the provided single-field map codecs using the assembler.
     */
    static <A> TupleMapCodec<A> of(Closure<A> assembler, CommentSpec spec, GetterMapCodec[] codecs) {
        return new TupleMapCodec(assembler, spec, codecs)
    }

    @Override
    <T> Stream<T> keys(DynamicOps<T> ops) {
        Stream<T> out = Stream.of()
        for (GetterMapCodec codec : codecs) {
            out = Stream.concat(out, codec.codec.<T>keys(ops))
        }
        return out
    }

    @Override
    <T> DataResult<O> decode(DynamicOps<T> ops, MapLike<T> input) {
        AtomicReference<String> error = new AtomicReference<>(null)
        Object[] args = codecs.collect {
            DataResult result = it.codec.decode(ops, input)
            if (result.error().present) {
                error.set(result.error().get().message())
            }
            return result.resultOrPartial({}).orElse(null)
        }.toArray()
        O obj = null
        try {
            obj = (args.inject(assembler) {cl, arg -> cl.curry(arg)} as Closure<O>).call()
        } catch (Exception e) {
            return DataResult.error(e.message)
        }
        return error.get()==null?DataResult.success(obj):DataResult.error(error.get(), obj)
    }

    @Override
    <T> RecordBuilder<T> encode(O input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        for (GetterMapCodec<?> codec : codecs) {
            prefix = codec.codec.encode(codec.getter.call(input), ops, prefix)
        }

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
                    return built.map { commentingOps.finalize(it, spec) }
                }
                return built
            }
        }
    }

    final static class GetterMapCodec<A> {
        final MapCodec<A> codec
        final Closure<A> getter
        private GetterMapCodec(MapCodec<A> codec, Closure<A> getter) {
            this.codec = codec
            this.getter = getter
        }
    }
}
