/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.extension.client

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.extension.EnvironmentExtension
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen

@CompileStatic
@EnvironmentExtension(EnvironmentExtension.Side.CLIENT)
class MinecraftExtensions {
    static void leftShift(Minecraft minecraft, Screen screen) {
        minecraft.setScreen(screen)
    }
}
