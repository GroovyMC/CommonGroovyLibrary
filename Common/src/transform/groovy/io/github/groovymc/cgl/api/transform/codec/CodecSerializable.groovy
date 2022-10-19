/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.transform.codec

import groovy.transform.CompileStatic
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * The {@code @CodecSerializable} annotation builds a codec based on the constructor and properties of a class. The transformation
 * bases this codec off the class's constructor which accepts the most parameters. Each of these parameters must have
 * the same name and type as a property defined on the object. This type can be the type parameter of any
 * {@link com.mojang.serialization.codecs.PrimitiveCodec} defined in {@link com.mojang.serialization.Codec}, any other
 * class with the {@link CodecSerializable} annotation, any class that exposes a codec with {@link ExposesCodec}, any
 * enum which extends {@link net.minecraft.util.StringRepresentable}, or any class {@code A} with a single public static
 * field of type {@code Codec<A>}, or any {@link List}, {@link java.util.Optional}, {@link Map},
 * {@link com.mojang.datafixers.util.Pair}, or {@link com.mojang.datafixers.util.Either} parameterized with a type or
 * types that satisfy the same requirements.
 *
 * The {@link ExposesCodec} annotation will be automatically applied with the proper value if it is not already present
 * on the class.
 *
 * Codec serialization supports commented codecs. Comments can be provided either with the {@link io.github.groovymc.cgl.api.codec.comments.Comment}
 * Annotation, or through groovydocs. If provided through groovydocs, the {@code groovy.attach.runtime} compiler flag must
 * be true. This can be set in gradle with {@code compileGroovy.groovyOptions.optimizationOptions.groovydoc = true}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE])
@GroovyASTTransformationClass('io.github.groovymc.cgl.impl.transform.codec.CodecSerializableTransformation')
@CompileStatic
@interface CodecSerializable {
    /**
     * The property at which the assembled codec will be stored. Can be accessed normally, or through
     * {@link CodecRetriever}.
     */
    String property() default '$CODEC'

    /**
     * Whether properties of names in camel case ({@code propertyName}) should be converted into snake case
     * ({@code property_name}). Note: this default will be switched to true in 1.20.
     */
    boolean camelToSnake() default false

    /**
     * Whether setting a default value for a field should make the matching constructor parameter optional. Note: this
     * default will be switched to true in 1.20.
     */
    boolean allowDefaultValues() default false
}