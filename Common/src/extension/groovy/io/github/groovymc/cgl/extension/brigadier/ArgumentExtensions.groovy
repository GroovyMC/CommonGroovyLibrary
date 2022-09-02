/*
 * Copyright (C) 2022 GroovyMC
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

//file:noinspection UnnecessaryQualifiedReference
package io.github.groovymc.cgl.extension.brigadier

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.ColorArgument
import net.minecraft.commands.arguments.ComponentArgument
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.UuidArgument
import net.minecraft.commands.arguments.selector.EntitySelector
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity

import static groovy.lang.Closure.DELEGATE_FIRST
import static io.github.groovymc.cgl.extension.brigadier.CommandExtensions.argument
import static io.github.groovymc.cgl.extension.brigadier.CommandExtensions.defaultGetter

/**
 * Extension methods for easier creation of default command arguments.
 * @author CommonGroovyLoader
 */
@CompileStatic
final class ArgumentExtensions {
    // region String
    static <S, T extends ArgumentBuilder<S, T>> T string(ArgumentBuilder<S, T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,java.lang.String>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,java.lang.String>'
    ) Closure closure) {
        argument(self, name, StringArgumentType.string(), defaultGetter(), closure)
    }
    static <S, T extends ArgumentBuilder<S, T>> T word(ArgumentBuilder<S, T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,java.lang.String>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,java.lang.String>'
    ) Closure closure) {
        argument(self, name, StringArgumentType.word(), defaultGetter(), closure)
    }
    static <S, T extends ArgumentBuilder<S, T>> T greedyString(ArgumentBuilder<S, T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,java.lang.String>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,java.lang.String>'
    ) Closure closure) {
        argument(self, name, StringArgumentType.greedyString(), defaultGetter(), closure)
    }
    // endregion

    // region Primitives
    static <S, T extends ArgumentBuilder<S, T>> T bool(ArgumentBuilder<S, T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,java.lang.Boolean>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,java.lang.Boolean>'
    ) Closure closure) {
        argument(self, name, BoolArgumentType.bool(), defaultGetter(), closure)
    }

    // region Floats
    static <S, T extends ArgumentBuilder<S, T>> T floatArg(ArgumentBuilder<S, T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,java.lang.Float>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,java.lang.Float>'
    ) Closure closure) {
        argument(self, name, FloatArgumentType.floatArg(), defaultGetter(), closure)
    }
    static <S, T extends ArgumentBuilder<S, T>> T floatArg(ArgumentBuilder<S, T> self, String name,
                                       final float min, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,java.lang.Float>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,java.lang.Float>'
    ) Closure closure) {
        argument(self, name, FloatArgumentType.floatArg(min), defaultGetter(), closure)
    }
    static <S, T extends ArgumentBuilder<S, T>> T floatArg(ArgumentBuilder<S, T> self, String name,
                                       final float min, final float max, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,java.lang.Float>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,java.lang.Float>'
    ) Closure closure) {
        argument(self, name, FloatArgumentType.floatArg(min, max), defaultGetter(), closure)
    }
    // endregion

    // region Double
    static <S, T extends ArgumentBuilder<S, T>> T doubleArg(ArgumentBuilder<S, T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,java.lang.Double>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,java.lang.Double>'
    ) Closure closure) {
        argument(self, name, DoubleArgumentType.doubleArg(), defaultGetter(), closure)
    }
    static <S, T extends ArgumentBuilder<S, T>> T doubleArg(ArgumentBuilder<S, T> self, String name,
                                       final double min, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,java.lang.Double>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,java.lang.Double>'
    ) Closure closure) {
        argument(self, name, DoubleArgumentType.doubleArg(min), defaultGetter(), closure)
    }
    static <S, T extends ArgumentBuilder<S, T>> T doubleArg(ArgumentBuilder<S, T> self, String name,
                                       final double min, final double max, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,java.lang.Double>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,java.lang.Double>'
    ) Closure closure) {
        argument(self, name, DoubleArgumentType.doubleArg(min, max), defaultGetter(), closure)
    }
    // endregion

    // region Integer
    static <S, T extends ArgumentBuilder<S, T>> T integer(ArgumentBuilder<S, T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,java.lang.Integer>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,java.lang.Integer>'
    ) Closure closure) {
        argument(self, name, IntegerArgumentType.integer(), defaultGetter(), closure)
    }
    static <S, T extends ArgumentBuilder<S, T>> T integer(ArgumentBuilder<S, T> self, String name,
                                                            final int min, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,java.lang.Integer>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,java.lang.Integer>'
    ) Closure closure) {
        argument(self, name, IntegerArgumentType.integer(min), defaultGetter(), closure)
    }
    static <S, T extends ArgumentBuilder<S, T>> T integer(ArgumentBuilder<S, T> self, String name,
                                                            final int min, final int max, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,java.lang.Integer>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,java.lang.Integer>'
    ) Closure closure) {
        argument(self, name, IntegerArgumentType.integer(min, max), defaultGetter(), closure)
    }
    // endregion

    // region Long
    static <S, T extends ArgumentBuilder<S, T>> T longArg(ArgumentBuilder<S, T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,java.lang.Long>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,java.lang.Long>'
    ) Closure closure) {
        argument(self, name, LongArgumentType.longArg(), defaultGetter(), closure)
    }
    static <S, T extends ArgumentBuilder<S, T>> T longArg(ArgumentBuilder<S, T> self, String name,
                                                          final long min, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,java.lang.Long>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,java.lang.Long>'
    ) Closure closure) {
        argument(self, name, LongArgumentType.longArg(min), defaultGetter(), closure)
    }
    static <S, T extends ArgumentBuilder<S, T>> T longArg(ArgumentBuilder<S, T> self, String name,
                                                          final long min, final long max, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,java.lang.Long>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,java.lang.Long>'
    ) Closure closure) {
        argument(self, name, LongArgumentType.longArg(min, max), defaultGetter(), closure)
    }
    // endregion

    // endregion

    // region Entities
    static <T extends ArgumentBuilder<CommandSourceStack, T>> T entity(ArgumentBuilder<CommandSourceStack, T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<net.minecraft.commands.CommandSourceStack,net.minecraft.world.entity.Entity>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<net.minecraft.commands.CommandSourceStack,net.minecraft.world.entity.Entity>'
    ) Closure closure) {
        CommandExtensions.<CommandSourceStack, T, EntitySelector, Entity>argument(self, name, EntityArgument.entity(), (String nm) -> (CommandContext<CommandSourceStack> context) -> context.getArgument(nm, EntitySelector).findSingleEntity(context.getSource()), closure)
    }
    static <T extends ArgumentBuilder<CommandSourceStack, T>> T entities(ArgumentBuilder<CommandSourceStack, T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<net.minecraft.commands.CommandSourceStack,java.util.List<? extends net.minecraft.world.entity.Entity>>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<net.minecraft.commands.CommandSourceStack,java.util.List<? extends net.minecraft.world.entity.Entity>>'
    ) Closure closure) {
        CommandExtensions.<CommandSourceStack, T, EntitySelector, List<? extends Entity>>argument(self, name, EntityArgument.entities(), (String nm) -> (CommandContext<CommandSourceStack> context) -> context.getArgument(nm, EntitySelector).findEntities(context.getSource()), closure)
    }

    static <T extends ArgumentBuilder<CommandSourceStack, T>> T player(ArgumentBuilder<CommandSourceStack, T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<net.minecraft.commands.CommandSourceStack,net.minecraft.server.level.ServerPlayer>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<net.minecraft.commands.CommandSourceStack,net.minecraft.server.level.ServerPlayer>'
    ) Closure closure) {
        CommandExtensions.<CommandSourceStack, T, EntitySelector, ServerPlayer>argument(self, name, EntityArgument.player(), (String nm) -> (CommandContext<CommandSourceStack> context) -> context.getArgument(nm, EntitySelector).findSinglePlayer(context.getSource()), closure)
    }
    static <T extends ArgumentBuilder<CommandSourceStack, T>> T players(ArgumentBuilder<CommandSourceStack, T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<net.minecraft.commands.CommandSourceStack,java.util.List<net.minecraft.server.level.ServerPlayer>>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<net.minecraft.commands.CommandSourceStack,java.util.List<net.minecraft.server.level.ServerPlayer>>'
    ) Closure closure) {
        CommandExtensions.<CommandSourceStack, T, EntitySelector, List<ServerPlayer>>argument(self, name, EntityArgument.entities(), (String nm) -> (CommandContext<CommandSourceStack> context) -> context.getArgument(nm, EntitySelector).findPlayers(context.getSource()), closure)
    }
    // endregion

    static <S, T extends ArgumentBuilder<S, T>> T color(ArgumentBuilder<S, T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,net.minecraft.ChatFormatting>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,net.minecraft.ChatFormatting>'
    ) Closure closure) {
        argument(self, name, ColorArgument.color(), defaultGetter(), closure)
    }
    static <S, T extends ArgumentBuilder<S, T>> T component(ArgumentBuilder<S, T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,net.minecraft.network.chat.Component>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,net.minecraft.network.chat.Component>'
    ) Closure closure) {
        argument(self, name, ComponentArgument.textComponent(), defaultGetter(), closure)
    }
    static <S, T extends ArgumentBuilder<S, T>> T uuid(ArgumentBuilder<S, T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,java.util.UUID>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.extension.brigadier.ArgumentGetter<S,java.util.UUID>'
    ) Closure closure) {
        argument(self, name, UuidArgument.uuid(), defaultGetter(), closure)
    }

}
