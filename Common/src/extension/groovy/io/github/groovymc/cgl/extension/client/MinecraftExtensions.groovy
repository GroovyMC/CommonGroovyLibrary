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
