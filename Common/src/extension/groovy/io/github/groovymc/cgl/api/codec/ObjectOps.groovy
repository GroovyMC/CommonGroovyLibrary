/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.codec


import com.mojang.datafixers.util.Pair
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.MapLike
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.NullObject

import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.stream.Stream
/**
 * A DynamicOps for converting to/from the formats used by groovy's JSON and TOML libraries - {@link Map}, {@link List},
 * {@link Number}, {@link Boolean}, {@link String}, {@link Date}, and null.
 * @see {@link groovy.json.JsonOutput}
 * @see {@link groovy.json.JsonSlurper}
 * @see {@link groovy.toml.TomlBuilder}
 * @see {@link groovy.toml.TomlSlurper}
 */
@CompileStatic
@Singleton(strict = false)
class ObjectOps implements DynamicOps<Object> {

    protected ObjectOps() {}

    @Override
    Object empty() {
        return NullObject.nullObject
    }

    @Override
    <U> U convertTo(DynamicOps<U> outOps, Object input) {
        if (input instanceof Map)
            return convertMap(outOps, input)
        if (input instanceof List)
            return convertList(outOps, input)
        if (input == NullObject.nullObject)
            return outOps.empty()
        if (input instanceof Boolean)
            return outOps.createBoolean(input)
        if (input instanceof String)
            return outOps.createString(input)
        if (input instanceof Number)
            return outOps.createNumeric(input)
        if (input instanceof Date)
            return outOps.createString(input.format("yyyy-MM-dd'T'HH:mm:ssZ"))
        throw new UnsupportedOperationException("${this.class.simpleName} was unable to convert a value: " + input)
    }

    @Override
    DataResult<Number> getNumberValue(Object i) {
        return i instanceof Number
                ? DataResult.success(i)
                : DataResult.error("Not a number: " + i) as DataResult<Number>
    }

    @Override
    Object createNumeric(Number i) {
        return i
    }

    @Override
    DataResult<String> getStringValue(Object input) {
        if (input instanceof Date)
            return DataResult.success(input.format("yyyy-MM-dd'T'HH:mm:ssZ"))
        return (input instanceof Map || input instanceof List) ?
                DataResult.error("Not a string: " + input) as DataResult<String> :
                DataResult.success(String.valueOf(input))
    }

    @Override
    Object createString(String value) {
        return value
    }

    @Override
    DataResult<Object> mergeToList(Object list, Object value) {
        if (!(list instanceof List) && list != this.empty())
            return DataResult.error("mergeToList called with not a list: " + list, list)
        final List result = []
        if (list != this.empty()) {
            List listAsCollection = list as List
            result.addAll(listAsCollection)
        }
        result.add(value)
        return DataResult.success(result) as DataResult<Object>
    }

    @Override
    DataResult<Object> mergeToMap(Object map, Object key, Object value) {
        if (!(map instanceof Map) && map != this.empty()) {
            return DataResult.error("mergeToMap called with not a map: " + map, map)
        }
        DataResult<String> stringResult = this.getStringValue(key)
        Optional<DataResult.PartialResult<String>> badResult = stringResult.error()
        if (badResult.isPresent())
            return DataResult.error("key is not a string: " + key, map)
        return stringResult.flatMap{
            final Map output = [:]
            if (map != this.empty()) {
                Map oldConfig = map as Map
                output.putAll(oldConfig)
            }
            output.put(it, value)
            return DataResult.success(output)
        } as DataResult<Object>
    }

    @Override
    DataResult<Stream<Pair<Object, Object>>> getMapValues(Object input) {
        if (!(input instanceof Map))
            return DataResult.error("Not a map: " + input)
        return DataResult.success(input.entrySet().stream().map {
            return Pair.of(it.key, it.value instanceof NullObject ? null : it.value)
        })
    }

    @Override
    DataResult<Consumer<BiConsumer<Object, Object>>> getMapEntries(Object input) {
        if (!(input instanceof Map))
            return DataResult.error("Not a map: " + input)
        return DataResult.<Consumer<BiConsumer<Object, Object>>>success {
            for (final Map.Entry<Object,Object> entry : input.entrySet()) {
                it.accept(entry.key, entry.value instanceof NullObject ? null : entry.value)
            }
        }
    }

    @Override
    DataResult<MapLike<Object>> getMap(Object input) {
        if (input instanceof Map) {
            Map map = (Map) input
            return DataResult.success(new MapLike<Object>() {
                @Override
                Object get(Object key) {
                    Object found = map.get(key)
                    return found instanceof NullObject ? null : found
                }

                @Override
                Object get(String key) {
                    Object found = map.get(key)
                    return found instanceof NullObject ? null : found
                }

                @Override
                Stream<Pair<Object, Object>> entries() {
                    map.entrySet().stream().map {
                        return Pair.of(it.key, it.value instanceof NullObject ? null : it.value)
                    }
                }

                @Override
                String toString() {
                    return "MapLike[${input}]"
                }
            })
        }
        return DataResult.error("Not a map: " + input)
    }

    @Override
    Object createMap(Stream<Pair<Object, Object>> map) {
        final Map result = [:]
        map.iterator().each {
            result.put(this.getStringValue(it.getFirst()).getOrThrow(false, {}), it.getSecond())
        }
        return result
    }

    @Override
    DataResult<Stream<Object>> getStream(Object input) {
        if (input instanceof List)
        {
            @SuppressWarnings("unchecked")
            List list = input as List
            return DataResult.success(list.stream().map {it instanceof NullObject ? null : it})
        }
        return DataResult.error("Not a list: " + input)
    }

    @Override
    DataResult<Consumer<Consumer<Object>>> getList(Object input) {
        if (input instanceof List) {
            return DataResult.<Consumer<Consumer<Object>>>success {
                for (final Object element : input) {
                    it.accept(element instanceof NullObject ? null : element)
                }
            }
        }
        return DataResult.error("Not a list: " + input)
    }

    @Override
    Object createList(Stream<Object> input) {
        return input.toList()
    }

    @Override
    Object remove(Object input, String key) {
        if (input instanceof Map) {
            final Map result = [:]
            input.entrySet().stream()
                    .filter {key != it.key}
                    .iterator()
                    .each {result.put(it.key, it.value)}
            return result
        }
        return input
    }
}
