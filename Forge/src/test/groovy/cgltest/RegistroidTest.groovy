/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

//file:noinspection unused
//file:noinspection GrFinalVariableAccess
//file:noinspection GrDeprecatedAPIUsage
package cgltest

import groovy.transform.CompileStatic
import groovy.transform.stc.POJO
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

@POJO
@CompileStatic
@SoundEventAddon
@Registroid({ [ForgeRegistries.BLOCKS, Registries.SOUND_EVENT] })
class RegistroidTest {
    static final Block SOME_TEST = new Block(BlockBehaviour.Properties.of(Material.DIRT))
    static final SoundEvent TEST_SOUND
    static final SoundEvent TEST_SOUND_2 = SoundEvent.createVariableRangeEvent(null)

    @Registroid
    static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, 'groovylicioustest')
    static final Item SOME_ITEM = new Item(new Item.Properties())

    @Registroid({ Registries.BLOCK })
    @BlockItemAddon({ new Item.Properties().setNoRepair() })
    static final class BlockItems {
        @RegistrationName('hello_world')
        static final Block A_BLOCK_WITH_BLOCK_ITEM = new Block(Block.Properties.of(Material.BUBBLE_COLUMN))
    }

    @RecipeTypeAddon
    @Registroid({ Registries.RECIPE_TYPE })
    static final class Recipes {
        static final RecipeType HELLO_WORLD
    }

    @Registroid(value = { Registries.ITEM }, includeInnerClasses = true)
    static final class InnersTest {
        static final class Inner1 {
            static final Item IT = new Item(new Item.Properties())
        }
        @RegistrationName('inner2/')
        static final class Inner2 {
            static final Item IT2 = new Item(new Item.Properties())
        }
    }
}