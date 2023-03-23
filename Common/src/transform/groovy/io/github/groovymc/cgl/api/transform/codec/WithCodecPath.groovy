/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.transform.codec

import groovy.transform.CompileStatic

/**
 * These path elements can be used with {@link WithCodec} to target individual nested type parameters with a codec.
 *
 * @deprecated Use {@link WithCodec#target()} instead.
 */
@Deprecated(forRemoval = true)
@CompileStatic
enum WithCodecPath {
    /**
     * Targets the type held by a {@link List}.
     */
    LIST,
    /**
     * Targets the type held by an {@link java.util.Optional}.
     */
    OPTIONAL,
    /**
     * Targets the type of the keys of a {@link Map}.
     */
    MAP_KEY,
    /**
     * Targets the type of the values of a {@link Map}
     */
    MAP_VAL,
    /**
     * Targets the type of the first value of a {@link com.mojang.datafixers.util.Pair}.
     */
    PAIR_FIRST,
    /**
     * Targets the type of the second value of a {@link com.mojang.datafixers.util.Pair}.
     */
    PAIR_SECOND,
    /**
     * Targets the type of the left value of a {@link com.mojang.datafixers.util.Either}.
     */
    EITHER_LEFT,
    /**
     * Targets the type of the right value of a {@link com.mojang.datafixers.util.Either}.
     */
    EITHER_RIGHT,
    /**
     * Targets every type parameter of any type using an exposed codec factory
     */
    FACTORY_PARAMETER
}