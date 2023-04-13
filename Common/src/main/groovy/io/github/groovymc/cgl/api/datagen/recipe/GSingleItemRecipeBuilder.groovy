/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.datagen.recipe

import com.google.gson.JsonObject
import groovy.contracts.Requires
import groovy.transform.CompileStatic
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.RequirementsStrategy
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import org.jetbrains.annotations.Nullable

import java.util.function.Consumer

@CompileStatic
class GSingleItemRecipeBuilder implements SimpleRecipeBuilder<GSingleItemRecipeBuilder> {
    private Ingredient ingredient
    private RecipeSerializer type

    GSingleItemRecipeBuilder() {}

    GSingleItemRecipeBuilder(RecipeSerializer type) {
        setType(type)
    }

    GSingleItemRecipeBuilder ingredient(Ingredient ingredient) {
        return setIngredient(ingredient)
    }

    GSingleItemRecipeBuilder setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient
        return this
    }

    GSingleItemRecipeBuilder type(RecipeSerializer type) {
        return setType(type)
    }

    GSingleItemRecipeBuilder setType(RecipeSerializer type) {
        this.type = type
        return this
    }

    @Requires({ this.ingredient && this.type })
    void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation recipeId) {
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR)
        finishedRecipeConsumer.accept(new Result(recipeId, this.type, this.group ?: '', this.ingredient, this.resultStack, this.advancement, recipeId.withPrefix("recipes/" + this.category.getFolderName() + "/")))
    }

    static class Result implements FinishedRecipe {
        private final ResourceLocation id
        private final String group
        private final Ingredient ingredient
        private final ItemStack result
        private final Advancement.Builder advancement
        private final ResourceLocation advancementId
        private final RecipeSerializer type

        Result(ResourceLocation resourceLocation, RecipeSerializer recipeSerializer, String group, Ingredient ingredient, ItemStack result, Advancement.Builder builder, ResourceLocation advId) {
            this.id = resourceLocation
            this.type = recipeSerializer
            this.group = group
            this.ingredient = ingredient
            this.result = result
            this.advancement = builder
            this.advancementId = advId
        }

        void serializeRecipeData(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty('group', this.group)
            }

            json.add('ingredient', this.ingredient.toJson())
            json.addProperty('result', BuiltInRegistries.ITEM.getKey(this.result.item).toString())
            json.addProperty("count", this.result.count)
        }

        ResourceLocation getId() {
            this.id
        }

        RecipeSerializer getType() {
            this.type
        }

        @Nullable
        JsonObject serializeAdvancement() {
            this.advancement.criteria.size() == 1 ? null : this.advancement.serializeToJson()
        }

        @Nullable
        ResourceLocation getAdvancementId() {
            this.advancementId
        }
    }
}
