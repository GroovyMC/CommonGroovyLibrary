/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package cgltest

import groovy.transform.CompileStatic
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag

@CompileStatic
class ExtensionTest {
    private static void doTest() {
        // TODO: this test probably needs a LevelStem or something because of how registries work in 1.19.3
        // assert Registry.ITEM[Tags.Items.SAND] instanceof HolderSet

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
