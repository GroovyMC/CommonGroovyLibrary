/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.datagen.recipe

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import groovy.transform.CompileStatic
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementRewards
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
class GShapedRecipeBuilder extends CraftingRecipeBuilder implements SimpleRecipeBuilder<GShapedRecipeBuilder> {
    private final List<String> rows = Lists.newArrayList()
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap()
    private boolean showNotification = true

    GShapedRecipeBuilder define(String symbol, TagKey<Item> tag) {
        this.define(symbol, Ingredient.of(tag))
    }

    GShapedRecipeBuilder define(String symbol, ItemLike item) {
        this.define(symbol, Ingredient.of(item))
    }

    GShapedRecipeBuilder define(String symbol, Ingredient ingredient) {
        if (this.key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '$symbol' is already defined!")
        } else if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined")
        } else {
            this.key.put(symbol.charAt(0), ingredient)
            return this
        }
    }

    GShapedRecipeBuilder pattern(String pattern) {
        this.pattern(pattern.stripIndent().split('\n'))
    }

    GShapedRecipeBuilder pattern(String... patterns) {
        patterns.each { pattern ->
            if (!this.rows.isEmpty() && pattern.length() !== this.rows[0].length()) {
                throw new IllegalArgumentException('Pattern must be the same width on every line!')
            } else {
                this.rows.add(pattern)
            }
        }
        return this
    }

    GShapedRecipeBuilder showNotification(boolean showNotification) {
        this.setShowNotification(showNotification)
        return this
    }

    void setShowNotification(boolean showNotification) {
        this.@showNotification = showNotification
    }

    void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation recipeId) {
        this.ensureValid(recipeId)
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR)
        finishedRecipeConsumer.accept(new Result(recipeId, this.resultStack, this.group ?: '', determineBookCategory(this.category), this.rows, this.key, this.advancement, recipeId.withPrefix("recipes/" + this.category.getFolderName() + "/"), this.showNotification))
    }

    private void ensureValid(ResourceLocation id) {
        if (this.rows.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe $id!")
        } else {
            Set<Character> set = Sets.newHashSet(this.key.keySet())
            set.remove(' ')
            final itr = this.rows.iterator()

            while (itr.hasNext()) {
                final row = itr.next()

                for (int i = 0; i < row.length(); ++i) {
                    char c = row.charAt(i)
                    if (!this.key.containsKey(c) && c !== ' ' as char) {
                        throw new IllegalStateException("Pattern in recipe $id uses undefined symbol '$c'")
                    }

                    set.remove(c)
                }
            }

            if (!set.isEmpty()) {
                throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe $id")
            }
        }
    }

    private static class Result extends CraftingRecipeBuilder.CraftingResult {
        private final ResourceLocation id
        private final ItemStack result
        private final String group
        private final List<String> pattern
        private final Map<Character, Ingredient> key
        private final Advancement.Builder advancement
        private final ResourceLocation advancementId
        private final boolean showNotification

        Result(ResourceLocation resourceLocation, ItemStack result, String string, CraftingBookCategory craftingBookCategory, List<String> list, Map<Character, Ingredient> map, Advancement.Builder builder, ResourceLocation resourceLocation2, boolean bl) {
            super(craftingBookCategory)
            this.id = resourceLocation
            this.result = result
            this.group = string
            this.pattern = list
            this.key = map
            this.advancement = builder
            this.advancementId = resourceLocation2
            this.showNotification = bl
        }

        void serializeRecipeData(JsonObject json) {
            super.serializeRecipeData(json)
            if (!this.group.isEmpty()) {
                json.addProperty('group', this.group)
            }

            JsonArray jsonArray = new JsonArray()
            this.pattern.each {
                jsonArray.add(it)
            }

            json.add('pattern', jsonArray)

            JsonObject jsonObject = new JsonObject()
            Iterator itr = this.key.entrySet().iterator()

            while (itr.hasNext()) {
                Map.Entry<Character, Ingredient> entry = (Map.Entry)itr.next()
                jsonObject.add(String.valueOf(entry.getKey()), ((Ingredient)entry.getValue()).toJson())
            }

            json.add('key', jsonObject)
            JsonObject jsonObject2 = new JsonObject()
            jsonObject2.addProperty('item', BuiltInRegistries.ITEM.getKey(this.result.item).toString())
            if (this.result.count > 1) {
                jsonObject2.addProperty('count', this.result.count)
            }
            // TODO - tag support

            json.add('result', jsonObject2)
            json.addProperty('show_notification', this.showNotification)
        }

        RecipeSerializer getType() {
            RecipeSerializer.SHAPED_RECIPE
        }

        ResourceLocation getId() {
            return this.id
        }

        @Nullable
        JsonObject serializeAdvancement() {
            return this.advancement.criteria.size() == 1 ? null : this.advancement.serializeToJson()
        }

        @Nullable
        ResourceLocation getAdvancementId() {
            return this.advancementId
        }
    }
}
