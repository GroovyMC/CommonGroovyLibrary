/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

//file:noinspection unused
package org.groovymc.cgl.api.extension

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import net.minecraft.nbt.*
import org.codehaus.groovy.runtime.DefaultGroovyMethods

/**
 * Extensions for working with {@linkplain Tag NBT}.
 * @author CommonGroovyLibrary
 */
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
