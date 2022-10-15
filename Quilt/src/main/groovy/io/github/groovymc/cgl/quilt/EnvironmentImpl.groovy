/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.quilt

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.environment.Environment
import io.github.groovymc.cgl.api.environment.Platform
import io.github.groovymc.cgl.api.environment.Side
import net.fabricmc.api.EnvType
import org.jetbrains.annotations.ApiStatus
import org.quiltmc.loader.api.QuiltLoader
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader

@CompileStatic
@ApiStatus.Internal
class EnvironmentImpl implements Environment {
    @Override
    Platform getPlatform() {
        return Platform.QUILT
    }

    @Override
    Side getSide() {
        return switch (MinecraftQuiltLoader.environmentType) {
            case EnvType.CLIENT -> Side.CLIENT
            case EnvType.SERVER -> Side.SERVER
        }
    }

    @Override
    boolean isProduction() {
        return !QuiltLoader.isDevelopmentEnvironment()
    }
}
