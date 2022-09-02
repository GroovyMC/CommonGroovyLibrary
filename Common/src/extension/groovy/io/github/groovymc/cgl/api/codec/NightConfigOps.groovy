/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.codec

import com.electronwill.nightconfig.core.CommentedConfig
import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.core.NullObject
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.codec.comments.CommentSpec
import io.github.groovymc.cgl.api.codec.comments.CommentingOps
import io.github.lukebemish.groovyduvet.wrapper.minecraft.api.codec.ObjectOps

import java.util.stream.Stream

@CompileStatic
abstract class NightConfigOps extends ObjectOps implements CommentingOps<Object> {

    protected abstract Config newConfig()
    protected abstract boolean isCommented()

    @Override
    <U> U convertTo(DynamicOps<U> outOps, Object input) {
        if (input instanceof Config)
            return convertMap(outOps, input)
        if (input instanceof Map)
            throw new UnsupportedOperationException("${this.class.simpleName} was unable to convert a value: $input")
        return super.convertTo(outOps, input)
    }

    @Override
    Object empty() {
        return NullObject.NULL_OBJECT
    }

    @Override
    DataResult<Object> mergeToMap(Object map, Object key, Object value) {
        if (!(map instanceof Config) && map != this.empty()) {
            return DataResult.error("mergeToMap called with not a map: " + map, map)
        }
        DataResult<String> stringResult = this.getStringValue(key)
        Optional<DataResult.PartialResult<String>> badResult = stringResult.error()
        if (badResult.isPresent())
            return DataResult.error("key is not a string: " + key, map)
        return stringResult.flatMap{
            final Config output = newConfig()
            if (map != this.empty()) {
                Config oldConfig = map as Config
                output.putAll(oldConfig)
            }
            output.set(it, value)
            return DataResult.success(output)
        } as DataResult<Object>
    }

    @Override
    DataResult<Stream<Pair<Object, Object>>> getMapValues(Object input) {
        if (!(input instanceof Config))
            return DataResult.error("Not a map: " + input)
        final Config config = input as Config
        return DataResult.success(config.entrySet().stream().map {
            return Pair.<Object, Object>of(it.key, it.value)
        })
    }

    @Override
    Object createMap(Stream<Pair<Object, Object>> map) {
        final Config result = newConfig()
        map.iterator().each {
            String key = this.getStringValue(it.getFirst()).getOrThrow(false, {})
            result.set(key, it.getSecond())
        }
        return result
    }

    @Override
    Object remove(Object input, String key) {
        if (input instanceof Config) {
            final Config result = newConfig()
            input.entrySet().stream()
                    .filter {key != it.key}
                    .iterator()
                    .each {result.set(it.key, it.value)}
            return result
        }
        return input
    }

    @Override
    DataResult<String> getStringValue(Object input) {
        if (input instanceof Config)
                return DataResult.<String>error("Not a string: " + input)
        return super.getStringValue(input)
    }

    Object finalize(Object input, CommentSpec spec) {
        if (commented && input instanceof CommentedConfig) {
            Config copy = newConfig()
            copy.putAll(input)
            if (copy instanceof CommentedConfig)
                input.valueMap().keySet().each {
                    if (spec.getComment(it) !== null)
                        ((CommentedConfig)copy).setComment(it, spec.getComment(it))
                }
            return copy
        }
        return input
    }
}
