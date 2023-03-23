package io.github.groovymc.cgl.api.transform.codec

import groovy.transform.CompileStatic
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@GroovyASTTransformationClass('io.github.groovymc.cgl.impl.transform.codec.ExposesCodecFactoryChecker')
@CompileStatic
@interface ExposesCodecFactory {
    /**
     * The name of parameterized method at which the codec factory is stored. Should point to a method with a number of
     * type parameters equal to the value of {@link #parameters()}, and a single parameter for each type parameter
     * {@code T} of type {@code Codec<T>}, which returns a codec parameterized by the outer type, parameterized by the
     * provided types.
     */
    String value()

    /**
     * @return the number of parameters the factory method takes, which should be equivalent to the number of type
     * parameters of the annotated class.
     */
    int parameters() default 1
}