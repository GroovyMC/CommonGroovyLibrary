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
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.AbstractCookingRecipe
import net.minecraft.world.item.crafting.CookingBookCategory
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.ItemLike
import org.jetbrains.annotations.Nullable

import java.util.function.Consumer

@CompileStatic
class GCookingRecipeBuilder implements SimpleRecipeBuilder<GCookingRecipeBuilder> {
    private CookingBookCategory bookCategory
    private Ingredient ingredient
    private float experience = 20
    private int cookingTime = 200
    private RecipeSerializer<? extends AbstractCookingRecipe> serializer

    GCookingRecipeBuilder() {
        this(RecipeSerializer.SMELTING_RECIPE)
    }

    GCookingRecipeBuilder(RecipeSerializer<? extends AbstractCookingRecipe> serializer) {
        this.serializer = serializer
    }

    GCookingRecipeBuilder serializer(RecipeSerializer<? extends AbstractCookingRecipe> serializer) {
        return setSerializer(serializer)
    }

    GCookingRecipeBuilder setSerializer(RecipeSerializer<? extends AbstractCookingRecipe> serializer) {
        this.serializer = serializer
        return this
    }

    GCookingRecipeBuilder ingredient(Ingredient ingredient) {
        setIngredient(ingredient)
        return this
    }

    void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient
    }

    void setExperience(float exp) {
        this.experience = exp
    }

    GCookingRecipeBuilder experience(float exp) {
        setExperience(exp)
        return this
    }

    void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime
    }

    GCookingRecipeBuilder cookingTime(int cookingTime) {
        setCookingTime(cookingTime)
        return this
    }

    @Requires({ ingredient && serializer && cookingTime > 0 })
    void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation recipeId) {
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion('has_the_recipe', RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);
        finishedRecipeConsumer.accept(new Result(recipeId, this.group ?: '', this.bookCategory ?: determineRecipeCategory(this.serializer, this.result), this.ingredient, this.result, this.experience, this.cookingTime, this.advancement, recipeId.withPrefix("recipes/" + this.category.getFolderName() + "/"), this.serializer));
    }

    private static CookingBookCategory determineRecipeCategory(RecipeSerializer<? extends AbstractCookingRecipe> serializer, ItemLike result) {
        if (serializer == RecipeSerializer.SMELTING_RECIPE) {
            if (result.asItem().isEdible()) {
                return CookingBookCategory.FOOD
            } else {
                return result.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC
            }
        } else if (serializer == RecipeSerializer.BLASTING_RECIPE) {
            return result.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC
        } else {
            return CookingBookCategory.MISC
        }
    }

    static class Result implements FinishedRecipe {
        private final ResourceLocation id
        private final String group
        private final CookingBookCategory category
        private final Ingredient ingredient
        private final Item result
        private final float experience
        private final int cookingTime
        private final Advancement.Builder advancement
        private final ResourceLocation advancementId
        private final RecipeSerializer<? extends AbstractCookingRecipe> serializer

        Result(ResourceLocation resourceLocation, String string, CookingBookCategory cookingBookCategory, Ingredient ingredient, Item item, float exp, int time, Advancement.Builder builder, ResourceLocation resourceLocation2, RecipeSerializer<? extends AbstractCookingRecipe> recipeSerializer) {
            this.id = resourceLocation
            this.group = string
            this.category = cookingBookCategory
            this.ingredient = ingredient
            this.result = item
            this.experience = exp
            this.cookingTime = time
            this.advancement = builder
            this.advancementId = resourceLocation2
            this.serializer = recipeSerializer
        }

        void serializeRecipeData(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty('group', this.group);
            }

            json.addProperty('category', this.category.getSerializedName())
            json.add('ingredient', this.ingredient.toJson())
            json.addProperty('result', BuiltInRegistries.ITEM.getKey(this.result).toString())
            json.addProperty('experience', this.experience)
            json.addProperty('cookingtime', this.cookingTime)
        }

        RecipeSerializer getType() {
            this.serializer
        }

        ResourceLocation getId() {
            this.id
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
