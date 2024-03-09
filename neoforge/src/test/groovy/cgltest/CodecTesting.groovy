package cgltest

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import org.groovymc.cgl.api.transform.codec.CodecSerializable
import org.groovymc.cgl.api.transform.codec.ExposeCodec
import org.groovymc.cgl.api.transform.codec.ExposeCodecFactory
import org.groovymc.cgl.api.transform.codec.WithCodec
import net.minecraft.resources.ResourceLocation

import java.util.function.Supplier

@CompileStatic
@CodecSerializable(property = "CODEC_GOES_HERE")
@TupleConstructor
class CodecTesting {
    @TupleConstructor
    class TestExposeFactory<T> implements Supplier<T> {
        T captured

        @Override
        T get() {
            return captured
        }

        @ExposeCodecFactory
        static <T> Codec<TestExposeFactory<T>> codec(Codec<T> codec) {
            return codec.<TestExposeFactory<T>>xmap(TestExposeFactory::new, TestExposeFactory::get)
        }
    }

    @TupleConstructor
    class TestExposeCodec {
        @ExposeCodec
        public static final Codec<TestExposeCodec> CODEC = Codec.STRING.<TestExposeCodec>xmap(TestExposeCodec::new, TestExposeCodec::getCaptured)

        final String captured
    }

    final String myProperty = 'default'

    @WithCodec(value = { ResourceLocation.CODEC.flatXmap({rl -> DataResult.<String>success(rl.toString())}, ResourceLocation::read) }, target = [1])
    @WithCodec(value = { ResourceLocation.CODEC.flatXmap({rl -> DataResult.<String>success(rl.toString())}, ResourceLocation::read) }, target = [0])
    Map<String,String> testWithCodec

    TestExposeFactory<ResourceLocation> testFactory

    TestExposeCodec testExposeCodec

    static void yes() {
        println CodecTesting.CODEC_GOES_HERE
    }
}
