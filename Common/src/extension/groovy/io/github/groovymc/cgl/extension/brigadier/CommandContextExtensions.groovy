/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.extension.brigadier

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import groovy.transform.CompileStatic
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

import java.util.function.Predicate

/**
 * Extensions for {@link com.mojang.brigadier.context.CommandContext CommandContexts} which use a {@linkplain net.minecraft.commands.CommandSourceStack}.
 * @author CommonGroovyLibrary
 */
@CompileStatic
final class CommandContextExtensions {
    // region Permissions
    /**
     * Checks if the stack has the {@linkplain Commands#LEVEL_ALL} permission.
     */
    static boolean isAnyone(CommandSourceStack self) {
        return self.hasPermission(Commands.LEVEL_ALL)
    }
    /**
     * Checks if the stack has the {@linkplain Commands#LEVEL_MODERATORS} permission.
     */
    static boolean isModerator(CommandSourceStack self) {
        return self.hasPermission(Commands.LEVEL_MODERATORS)
    }
    /**
     * Checks if the stack has the {@linkplain Commands#LEVEL_GAMEMASTERS} permission.
     */
    static boolean isGameMaster(CommandSourceStack self) {
        return self.hasPermission(Commands.LEVEL_GAMEMASTERS)
    }
    /**
     * Checks if the stack has the {@linkplain Commands#LEVEL_ADMINS} permission.
     */
    static boolean isAdmin(CommandSourceStack self) {
        return self.hasPermission(Commands.LEVEL_ADMINS)
    }
    /**
     * Checks if the stack has the {@linkplain Commands#LEVEL_OWNERS} permission.
     */
    static boolean isOwner(CommandSourceStack self) {
        return self.hasPermission(Commands.LEVEL_OWNERS)
    }
    // endregion

    // region PredicatePermissions
    /**
     * Creates a Predicate that checks if the stack has the {@linkplain Commands#LEVEL_ALL} permission.
     */
    static Predicate<CommandSourceStack> getAnyone(ArgumentBuilder self) {
        return (CommandSourceStack stack) -> stack.hasPermission(Commands.LEVEL_ALL)
    }
    /**
     * Creates a Predicate that checks if the stack has the {@linkplain Commands#LEVEL_MODERATORS} permission.
     */
    static Predicate<CommandSourceStack> getModerator(ArgumentBuilder self) {
        return (CommandSourceStack stack) -> stack.hasPermission(Commands.LEVEL_MODERATORS)
    }
    /**
     * Creates a Predicate that checks if the stack has the {@linkplain Commands#LEVEL_GAMEMASTERS} permission.
     */
    static Predicate<CommandSourceStack> getGameMaster(ArgumentBuilder self) {
        return (CommandSourceStack stack) -> stack.hasPermission(Commands.LEVEL_GAMEMASTERS)
    }
    /**
     * Creates a Predicate that checks if the stack has the {@linkplain Commands#LEVEL_ADMINS} permission.
     */
    static Predicate<CommandSourceStack> getAdmin(ArgumentBuilder self) {
        return (CommandSourceStack stack) -> stack.hasPermission(Commands.LEVEL_ADMINS)
    }
    /**
     * Creates a Predicate that checks if the stack has the {@linkplain Commands#LEVEL_OWNERS} permission.
     */
    static Predicate<CommandSourceStack> getOwner(ArgumentBuilder self) {
        return (CommandSourceStack stack) -> stack.hasPermission(Commands.LEVEL_OWNERS)
    }
    // endregion

    @Nullable
    static ServerPlayer getPlayer(CommandContext<CommandSourceStack> self) {
        self.source.getPlayer()
    }
    @Nullable
    static Entity getEntity(CommandContext<CommandSourceStack> self) {
        self.source.entity
    }
    @NotNull
    static ServerLevel getLevel(CommandContext<CommandSourceStack> self) {
        self.source.level
    }
    @NotNull
    static MinecraftServer getServer(CommandContext<CommandSourceStack> self) {
        self.source.server
    }

    static void sendSuccess(CommandSourceStack self, Component message) {
        self.sendSuccess(message, false)
    }
    static void sendSuccess(CommandContext<CommandSourceStack> self, Component message, boolean allowLogging = false) {
        self.source.sendSuccess(message, allowLogging)
    }
    static void sendFailure(CommandContext<CommandSourceStack> self, Component message) {
        self.source.sendFailure(message)
    }
}
