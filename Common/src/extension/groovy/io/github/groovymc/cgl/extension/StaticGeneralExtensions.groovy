package io.github.groovymc.cgl.extension

import groovy.transform.CompileStatic
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType

@CompileStatic
class StaticGeneralExtensions {
    static <T extends Recipe> RecipeType<T> simple(ResourceLocation name) {
        final actualName = name.toString()
        return new RecipeType<T>() {
            @Override
            String toString() {
                return actualName
            }
        }
    }
}
