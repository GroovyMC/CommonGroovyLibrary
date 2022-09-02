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

package cgltest


import groovy.transform.CompileStatic
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraftforge.common.Tags

@CompileStatic
class ExtensionTest {
    private static void doTest() {
        assert Registry.ITEM[Tags.Items.SAND] instanceof HolderSet

        Minecraft.instance << new ChatScreen('yes')

        assert Tag.from('hi') instanceof StringTag
        final Map<String, Object> inMap = [
                'no': 'yes',
                'yes': 12
        ] as Map<String, Object>
        final tag = CompoundTag.of([
                '12': 12,
        ] as Map<String, ?>) + inMap
        assert tag['12'] === 12
        tag.remove('12')
        inMap['test'] = 89
        tag['test'] = 89
        assert tag as Map == inMap
    }
}
