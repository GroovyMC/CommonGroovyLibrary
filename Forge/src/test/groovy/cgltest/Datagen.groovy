/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package cgltest

import com.matyrobbrt.gml.bus.EventBusSubscriber
import com.matyrobbrt.gml.bus.type.ModBus
import groovy.time.TimeCategory
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import io.github.groovymc.cgl.api.datagen.recipe.GRecipeProvider
import io.github.groovymc.cgl.api.datagen.recipe.GShapelessRecipeBuilder
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

@CompileStatic
@EventBusSubscriber(ModBus)
class Datagen extends GRecipeProvider {
    @SubscribeEvent @PackageScope
    static void onDatagen(final GatherDataEvent event) {
        event.generator.addProvider(event.includeServer(), new Datagen(event.generator.packOutput))
    }

    Datagen(PackOutput packOutput) {
        super(packOutput, 'cgltest')
    }

    @Override
    protected void buildRecipes() {
        shaped {
            result Items.ACACIA_STAIRS count 2
            category = RecipeCategory.COMBAT
            group = 'yes'

            pattern 'AAA'
            define 'A', Items.ACACIA_LOG
        } save 'my_stairs'

        recipe(GShapelessRecipeBuilder) {
            result Items.ACACIA_BOAT
            category = RecipeCategory.REDSTONE
            group = 'no'

            requires ItemTags.COAL_ORES
        }.save()

        shapeless {
            result Items.SPRUCE_BUTTON * 12
            requires Items.AZALEA
        } save 'sprucex12'

        normal(SimpleCookingRecipeBuilder.blasting(
                Items.ACACIA_BOAT.ingredient(),
                RecipeCategory.DECORATIONS,
                Items.ALLIUM,
                12f,
                2000
        )) {
            unlockedBy('yes', has(Items.ALLIUM))
        } save new ResourceLocation('anothermod:yesyes')

        campfireCooking {
            result Items.ACACIA_LOG
            ingredient Items.AZALEA as Ingredient
            category RecipeCategory.BREWING
        }

        stonecutting {
            result Items.END_STONE_BRICK_SLAB
            ingredient Items.PINK_CANDLE.ingredient()
        } save 'test_stonecutting'

        dynamic()

        saveForgotten()
    }

    @CompileDynamic
    private void dynamic() {
        smelting {
            result Items.IRON_AXE
            ingredient Items.BAMBOO.ingredient()
            use (TimeCategory) {
                cookingTime 13.seconds
            }
            experience 4f
        }
    }
}
