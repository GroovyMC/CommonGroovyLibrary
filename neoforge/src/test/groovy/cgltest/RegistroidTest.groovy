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
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import org.groovymc.cgl.api.transform.registroid.BlockItemAddon
import org.groovymc.cgl.api.transform.registroid.RecipeTypeAddon
import org.groovymc.cgl.api.transform.registroid.RegistrationName
import org.groovymc.cgl.api.transform.registroid.Registroid
import org.groovymc.cgl.api.transform.registroid.SoundEventAddon

@POJO
@CompileStatic
@SoundEventAddon
@Registroid({ [ForgeRegistries.BLOCKS, Registries.SOUND_EVENT] })
class RegistroidTest {
    static final Block SOME_TEST = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).sound(SoundType.ROOTED_DIRT))
    static final SoundEvent TEST_SOUND
    static final SoundEvent TEST_SOUND_2 = SoundEvent.createVariableRangeEvent(null)

    @Registroid
    static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, 'groovylicioustest')
    static final Item SOME_ITEM = new Item(new Item.Properties())

    @Registroid({ Registries.BLOCK })
    @BlockItemAddon({ new Item.Properties().setNoRepair() })
    static final class BlockItems {
        @RegistrationName('hello_world')
        static final Block A_BLOCK_WITH_BLOCK_ITEM = new Block(Block.Properties.of().mapColor(MapColor.COLOR_BLACK))
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
