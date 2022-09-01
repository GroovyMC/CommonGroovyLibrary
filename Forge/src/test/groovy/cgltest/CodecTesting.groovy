package cgltest

import groovy.transform.CompileStatic
import io.github.lukebemish.groovyduvet.wrapper.minecraft.api.codec.CodecSerializable

@CompileStatic
@CodecSerializable
class CodecTesting {
    String myProperty
    static void yes() {
        CodecTesting.CODEC
    }
}
