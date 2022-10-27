/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.transform.codec

import com.mojang.serialization.Codec

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
/**
 * Allows codecs to be specified for types which {@link CodecSerializable} would otherwise not be able to use. This
 * annotation takes two arguments; a closure which provides the annotation to use, and an optional list that defines
 * where the parameter being annotated is. This annotation can be placed on either the parameter in the constructor or
 * this field or getter for the property matching the parameter.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.PARAMETER,ElementType.FIELD,ElementType.METHOD])
@interface WithCodec {
    /**
     * A closure that returns the codec to use for the targeted elements.
     */
    Class<? extends Closure<Codec>> value()

    /**
     * The path to the type to target within the type parameter structure of the annotated element. For instance, in the
     * following:
     * <pre>
     *     {@literal @}WithCodec(value = { IntProvider.NON_NEGATIVE_CODEC },
     *                 path = [WithCodecPath.LIST, WithCodecPath.PAIR_LEFT])
     *     List{@literal <}Pair{@literal <}IntProvider,Boolean{@literal >>} getPairs() {
     *         ...
     *     }
     * </pre>
     * The {@code IntProvider} within the {@code Pair} within the {@code List} is targetted, and the transformer is told
     * to use the closure to provide the codec.
     *
     * This parameter is optional; if not present or an empty list, the root type is targeted.
     */
    WithCodecPath[] path() default []
}