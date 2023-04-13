/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.datagen.recipe

import com.google.common.collect.Lists
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import groovy.transform.CompileStatic
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.RequirementsStrategy
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.recipes.CraftingRecipeBuilder
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.ItemLike
import org.jetbrains.annotations.Nullable

import java.util.function.Consumer

@CompileStatic
class GShapelessRecipeBuilder extends CraftingRecipeBuilder implements SimpleRecipeBuilder<GShapelessRecipeBuilder> {
    private final List<Ingredient> ingredients = Lists.newArrayList()

    GShapelessRecipeBuilder requires(TagKey<Item> tag) {
        this.requires(Ingredient.of(tag))
    }

    GShapelessRecipeBuilder requires(ItemLike item) {
        this.requires(item, 1)
    }

    GShapelessRecipeBuilder requires(ItemLike item, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.requires(Ingredient.of(new ItemLike[]{item}))
        }
        return this
    }

    GShapelessRecipeBuilder requires(Ingredient ingredient) {
        return this.requires(ingredient, 1)
    }

    GShapelessRecipeBuilder requires(Ingredient ingredient, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.ingredients.add(ingredient)
        }
        return this
    }

    void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation recipeId) {
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);
        finishedRecipeConsumer.accept(new Result(recipeId, this.resultStack, this.group ?: '', determineBookCategory(this.category), this.ingredients, this.advancement, recipeId.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    static class Result extends CraftingRecipeBuilder.CraftingResult {
        private final ResourceLocation id;
        private final ItemStack result
        private final String group;
        private final List<Ingredient> ingredients;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        Result(ResourceLocation resourceLocation, ItemStack result, String string, CraftingBookCategory craftingBookCategory, List<Ingredient> list, Advancement.Builder builder, ResourceLocation resourceLocation2) {
            super(craftingBookCategory)
            this.id = resourceLocation
            this.result = result
            this.group = string
            this.ingredients = list
            this.advancement = builder
            this.advancementId = resourceLocation2
        }

        void serializeRecipeData(JsonObject json) {
            super.serializeRecipeData(json)
            if (!this.group.isEmpty()) {
                json.addProperty('group', this.group)
            }

            JsonArray jsonArray = new JsonArray()
            this.ingredients.each {
                jsonArray.add(it.toJson())
            }

            json.add('ingredients', jsonArray)
            final jsonObject = new JsonObject()
            jsonObject.addProperty('item', BuiltInRegistries.ITEM.getKey(this.result.item).toString())
            if (this.result.count > 1) {
                jsonObject.addProperty('count', this.result.count)
            }

            json.add('result', jsonObject)
        }

        RecipeSerializer getType() {
            return RecipeSerializer.SHAPELESS_RECIPE
        }

        ResourceLocation getId() {
            return this.id
        }

        @Nullable
        JsonObject serializeAdvancement() {
            return this.advancement.criteria.size() === 1 ? null : this.advancement.serializeToJson()
        }

        @Nullable
        ResourceLocation getAdvancementId() {
            return this.advancementId
        }
    }
}
