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
package io.github.groovymc.cgl.extension

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import net.minecraft.nbt.*
import org.codehaus.groovy.runtime.DefaultGroovyMethods

@CompileStatic
class NBTExtensions {
    // region Setters
    static void putAt(CompoundTag self, String key, Tag value) {
        self.put(key, value)
    }
    static void putAt(CompoundTag self, String key, boolean value) {
        self.putBoolean(key, value)
    }
    static void putAt(CompoundTag self, String key, int value) {
        self.putInt(key, value)
    }
    static void putAt(CompoundTag self, String key, int[] value) {
        self.putIntArray(key, value)
    }
    static void putAt(CompoundTag self, String key, List value) {
        self.put(key, StaticNBTExtensions.from(null, value))
    }
    static void putAt(CompoundTag self, String key, UUID value) {
        self.putUUID(key, value)
    }
    static void putAt(CompoundTag self, String key, byte value) {
        self.putByte(key, value)
    }
    static void putAt(CompoundTag self, String key, byte[] value) {
        self.putByteArray(key, value)
    }
    static void putAt(CompoundTag self, String key, short value) {
        self.putShort(key, value)
    }
    static void putAt(CompoundTag self, String key, long value) {
        self.putLong(key, value)
    }
    static void putAt(CompoundTag self, String key, long[] value) {
        self.putLongArray(key, value)
    }
    static void putAt(CompoundTag self, String key, float value) {
        self.putFloat(key, value)
    }
    static void putAt(CompoundTag self, String key, double value) {
        self.putDouble(key, value)
    }
    static void putAt(CompoundTag self, String key, String value) {
        self.putString(key, value)
    }

    static Object getAt(CompoundTag self, String key) {
        return underlyingValue(self.get(key))
    }
    // endregion

    // region Concatenation
    static CompoundTag plus(CompoundTag self, CompoundTag other) {
        final tag = new CompoundTag()
        self.allKeys.forEach(it -> tag.put(it, self.get(it)))
        other.allKeys.forEach(it -> tag.put(it, other.get(it)))
        return tag
    }
    static CompoundTag plus(CompoundTag self, Map<String, ?> other) {
        final tag = new CompoundTag()
        self.allKeys.forEach(it -> tag.put(it, self.get(it)))
        other.forEach((key, val) -> tag.put(key, StaticNBTExtensions.from(null, val)))
        return tag
    }
    // endregion

    @TypeChecked(TypeCheckingMode.SKIP)
    static <T> T asType(CompoundTag self, Class<T> type) {
        return switch (type) {
            case Map -> (T) new HashMap<>().tap { map ->
                self.allKeys.each { map[it] = underlyingValue(self.get(it)) }
            }
            default -> (T) DefaultGroovyMethods.asType(self, type)
        }
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    static Object underlyingValue(Tag tag) {
        return switch (tag) {
            case StringTag -> tag.asString
            case IntTag -> tag.asInt
            case DoubleTag -> tag.asDouble
            case ByteTag -> tag.asByte
            case FloatTag -> tag.asFloat
            case ShortTag -> tag.asShort
            case LongTag -> tag.asLong
            case CollectionTag -> new ArrayList<>().tap { list ->
                tag.each { list.add(it instanceof Tag ? underlyingValue(it) : it) }
            }
            default -> tag
        }
    }

}
