package org.groovymc.cgl.api.transform.codec

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Collector annotation for {@link WithCodec}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.PARAMETER,ElementType.FIELD,ElementType.METHOD])
@interface WithCodecs {
    WithCodec[] value()
}
