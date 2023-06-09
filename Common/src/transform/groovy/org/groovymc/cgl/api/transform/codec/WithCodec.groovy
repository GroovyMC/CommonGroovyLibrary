/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.cgl.api.transform.codec


import java.lang.annotation.*
/**
 * Allows codecs to be specified for types which {@link CodecSerializable} would otherwise not be able to use. This
 * annotation takes two arguments; a closure which provides the codec to use, and an optional list that defines where
 * the parameter being annotated is. This annotation can be placed on either the parameter in the constructor or this
 * field or getter for the property matching the parameter.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.PARAMETER,ElementType.FIELD,ElementType.METHOD])
@Repeatable(WithCodecs)
@interface WithCodec {
    /**
     * A closure that returns the codec to use for the targeted elements.
     */
    Class<? extends Closure> value()

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
     *
     * @deprecated Use {@link WithCodec#target()} instead.
     */
    @Deprecated(forRemoval = true)
    WithCodecPath[] path() default []

    /**
     * A series of indices defining the path of the codec which should be replaced. For instance, in the following:
     * <pre>
     *     {@literal @}WithCodec(value = { IntProvider.NON_NEGATIVE_CODEC },
     *                 target = [0,0])
     *     List{@literal <}Pair{@literal <}IntProvider,Boolean{@literal >>} getPairs() {
     *         ...
     *     }
     * </pre>
     * The {@code IntProvider} within the {@code Pair} within the {@code List} is targetted, and the transformer is told
     * to use the closure to replace the codec. The provided indices correspond to the indices of the type parameters..
     */
    int[] target() default []
}