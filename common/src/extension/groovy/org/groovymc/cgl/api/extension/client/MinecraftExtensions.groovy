package org.groovymc.cgl.api.extension.client


import groovy.transform.CompileStatic
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import org.groovymc.cgl.api.extension.EnvironmentExtension

@CompileStatic
@EnvironmentExtension(EnvironmentExtension.Side.CLIENT)
class MinecraftExtensions {
    static void leftShift(Minecraft minecraft, Screen screen) {
        minecraft.setScreen(screen)
    }
}
