/*
 * Copyright (C) 2022 GroovyMC
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

//file:noinspection unused
//file:noinspection GrFinalVariableAccess
//file:noinspection GrDeprecatedAPIUsage
package cgltest

import groovy.transform.CompileStatic
import groovy.transform.stc.POJO
import io.github.groovymc.cgl.transform.Registroid
import io.github.groovymc.cgl.transform.registroid.RegistrationName
import io.github.groovymc.cgl.transform.registroid.blockitem.BlockItemAddon
import io.github.groovymc.cgl.transform.registroid.recipetype.RecipeTypeAddon
import io.github.groovymc.cgl.transform.registroid.sound.SoundEventAddon
import net.minecraft.core.Registry
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
@Registroid({ [ForgeRegistries.BLOCKS, Registry.SOUND_EVENT_REGISTRY] })
class RegistroidTest {
    static final Block SOME_TEST = new Block(BlockBehaviour.Properties.of(Material.DIRT))
    static final SoundEvent TEST_SOUND
    static final SoundEvent TEST_SOUND_2 = new SoundEvent(null, .1f)

    @Registroid
    static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registry.ITEM_REGISTRY, 'groovylicioustest')
    static final Item SOME_ITEM = new Item(new Item.Properties())

    @Registroid({ Registry.BLOCK_REGISTRY })
    @BlockItemAddon({ new Item.Properties().setNoRepair() })
    static final class BlockItems {
        @RegistrationName('hello_world')
        static final Block A_BLOCK_WITH_BLOCK_ITEM = new Block(Block.Properties.of(Material.BUBBLE_COLUMN))
    }

    @RecipeTypeAddon
    @Registroid({ Registry.RECIPE_TYPE })
    static final class Recipes {
        static final RecipeType HELLO_WORLD
    }

    @Registroid(value = { Registry.ITEM }, includeInnerClasses = true)
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