/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.extension.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import groovy.lang.Closure;

import java.util.function.Function;

/**
 * An interface used by {@linkplain CommandExtensions#argument(ArgumentBuilder, String, ArgumentType, Function, Closure)} and the likes
 * in order to provide an easy way of retrieving arguments.
 *
 * @param <S> the type of the source
 * @param <Z> the type of the argument
 */
@FunctionalInterface
interface ArgumentGetter<S, Z> {
    /**
     * Gets the argument from the given {@code context}.
     *
     * @param context the context to retrieve the argument for
     * @return the argument
     */
    Z call(CommandContext<S> context);
}