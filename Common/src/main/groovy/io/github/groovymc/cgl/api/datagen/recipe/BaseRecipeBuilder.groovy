/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.datagen.recipe

import groovy.transform.CompileStatic
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike

@CompileStatic
trait BaseRecipeBuilder extends SaveableRecipe implements RecipeBuilder {
    ItemStack result

    ItemStack result(ItemLike result) {
        this.result = result.asItem().getDefaultInstance()
        return this.result
    }

    void setResult(ItemLike result) {
        this.result(result)
    }

    ItemStack result(ItemStack result) {
        this.result = result
        return this.result
    }

    void setResult(ItemStack result) {
        this.result(result)
    }

    Item getResult() {
        return this.@result.item
    }

    ItemStack getResultStack() {
        return this.@result
    }

    /**
     * Saves this recipe to a location representing the {@link #getResult() result's} registry name.
     */
    void save() {
        this.save(BuiltInRegistries.ITEM.getKey(getResult()))
    }

    /**
     * {@inheritDoc}
     */
    void save(ResourceLocation location) {
        provider.forgottenRecipes.remove(this)
        this.save(this.getProvider().writer, location)
    }
}
