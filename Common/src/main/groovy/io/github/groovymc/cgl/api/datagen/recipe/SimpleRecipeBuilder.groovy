/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.datagen.recipe

import groovy.transform.CompileStatic
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.CriterionTriggerInstance
import net.minecraft.data.recipes.RecipeCategory
import org.jetbrains.annotations.Nullable

@CompileStatic
trait SimpleRecipeBuilder<T extends SimpleRecipeBuilder> extends BaseRecipeBuilder {
    private RecipeCategory category = RecipeCategory.MISC
    @Nullable
    private String group
    private final Advancement.Builder advancement = Advancement.Builder.advancement()

    RecipeCategory getCategory() {
        return this.@category
    }
    void setCategory(RecipeCategory category) {
        this.@category = category
    }

    T category(RecipeCategory category) {
        setCategory(category)
        return (T) this
    }

    String getGroup() {
        return this.@group
    }
    void setGroup(String group) {
        this.@group = group
    }

    T group(String group) {
        setGroup(group)
        return (T) this
    }

    T unlockedBy(String criterionName, CriterionTriggerInstance criterionTrigger) {
        this.@advancement.addCriterion(criterionName, criterionTrigger)
        return (T) this
    }

    Advancement.Builder getAdvancement() {
        return this.@advancement
    }
}