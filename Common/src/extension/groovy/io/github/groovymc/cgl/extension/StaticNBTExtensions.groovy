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

package io.github.groovymc.cgl.extension

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.ShortTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag

@CompileStatic
 final class StaticNBTExtensions {
    static CompoundTag of(CompoundTag self, Map<String, ?> map) {
        final tag = new CompoundTag()
        map.forEach((key, val) -> tag.put(key, from(null, val)))
        return tag
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    static Tag from(Tag self, Object toConvert) {
        return switch (toConvert) {
            case Tag -> toConvert
            case String -> StringTag.valueOf(toConvert)
            case int, Integer -> IntTag.valueOf(toConvert as Integer)
            case long, Long -> LongTag.valueOf(toConvert as Long)
            case short, Short -> ShortTag.valueOf(toConvert as Short)
            case byte, ByteTag -> ByteTag.valueOf(toConvert as Byte)
            case double, DoubleTag -> DoubleTag.valueOf(toConvert as Double)
            case float, FloatTag -> FloatTag.valueOf(toConvert as Float)
            case Collection -> new ListTag().tap { list ->
                toConvert.each { list.add(from(null, it)) }
            }
            default -> (Tag) null
        }
    }
}
