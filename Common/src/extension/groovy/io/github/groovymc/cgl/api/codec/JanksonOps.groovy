package io.github.groovymc.cgl.api.codec

import blue.endless.jankson.*
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.MapLike
import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.codec.comments.CommentSpec
import io.github.groovymc.cgl.api.codec.comments.CommentingOps
import org.jetbrains.annotations.Nullable

import java.util.stream.Stream

/**
 * A {@link CommentingOps} that reads or writes to Jankson's JSON elements.
 */
@CompileStatic
class JanksonOps implements CommentingOps<JsonElement> {

    /**
     * A JanksonOps that attaches comments to its encoded configs
     */
    static final JanksonOps COMMENTED = new JanksonOps(true)

    /**
     * A JanksonOps that does not attach comments to its encoded configs
     */
    static final JanksonOps UNCOMMENTED = new JanksonOps(false)

    final boolean commented

    private JanksonOps(boolean commented) {
        this.commented = commented
    }

    @Override
    JsonElement empty() {
        return JsonNull.INSTANCE
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, JsonElement input) {
        if (input instanceof JsonObject)
            return convertMap(outOps, input)
        if (input instanceof JsonArray)
            return convertList(outOps, input)
        if (input instanceof JsonNull)
            return outOps.empty();
        if (input instanceof JsonPrimitive) {
            Object value = input.getValue()
            if (value instanceof String)
                return outOps.createString(value)
            if (value instanceof Number)
                return outOps.createNumeric(value)
            if (value instanceof Boolean)
                return outOps.createBoolean(value)
        }
        throw new UnsupportedOperationException("${this.class.simpleName} was unable to convert a value: $input")
    }

    @Override
    DataResult<Number> getNumberValue(JsonElement input) {
        if (input instanceof JsonPrimitive) {
            Object value = input.getValue()
            if (value instanceof Number)
                return DataResult.success(value)
            if (value instanceof Boolean)
                return DataResult.<Number>success(Boolean.TRUE == value ? 1 : 0)
        }
        return DataResult.error("Not a number: $input")
    }

    @Override
    JsonElement createNumeric(Number i) {
        return new JsonPrimitive(i)
    }

    @Override
    DataResult<String> getStringValue(JsonElement input) {
        if (input instanceof JsonPrimitive) {
            Object value = input.getValue()
            if (value instanceof String)
                return DataResult.success(value)
        }
        return DataResult.error("Not a string: $input")
    }

    @Override
    JsonElement createString(String value) {
        return new JsonPrimitive(value)
    }

    @Override
    DataResult<JsonElement> mergeToList(JsonElement list, JsonElement value) {
        if (list instanceof JsonArray || list == empty()) {
            JsonArray result = new JsonArray()
            if (list != empty()) {
                JsonArray array = (JsonArray) list
                result.addAll(array)
            }
            result.add(value)
            return DataResult.<JsonElement>success(result)
        }
        return DataResult.error("mergeToList called with not a list: $list", list)
    }

    @Override
    DataResult<JsonElement> mergeToList(final JsonElement list, final List<JsonElement> values) {
        if (list instanceof JsonArray || list == empty()) {
            JsonArray result = new JsonArray()
            if (list != empty()) {
                result.addAll((JsonArray) list)
            }
            result.addAll(values)
            return DataResult.<JsonElement>success(result)
        }
        return DataResult.error("mergeToList called with not a list: $list", list)
    }

    @Override
    DataResult<JsonElement> mergeToMap(JsonElement map, JsonElement key, JsonElement value) {
        if (!(map instanceof JsonObject) && map != empty())
            return DataResult.error("mergeToMap called with not a map: $map", map)
        if (!(key instanceof JsonPrimitive) || !(((JsonPrimitive)key).getValue() instanceof String))
            return DataResult.error("key is not a string: $key", map)
        JsonObject output = new JsonObject()
        if (map != empty()) {
            JsonObject jsonObject = (JsonObject) map
            output.putAll(jsonObject)
        }
        output.put((String) ((JsonPrimitive)key).getValue(), value)
        return DataResult.<JsonElement>success(output)
    }

    @Override
    DataResult<JsonElement> mergeToMap(JsonElement map, MapLike<JsonElement> values) {
        if (!(map instanceof JsonObject) && map != empty())
            return DataResult.error("mergeToMap called with not a map: $map", map)
        JsonObject output = new JsonObject()
        if (map != empty())
            output.putAll((JsonObject) map)
        List<JsonElement> missed = new ArrayList<>()
        values.entries().forEach(entry -> {
            if (entry.getFirst() instanceof JsonPrimitive && ((JsonPrimitive)entry.getFirst()).getValue() instanceof String)
                output.put(((String)((JsonPrimitive)entry.getFirst()).getValue()), entry.getSecond())
            else
                missed.add(entry.getFirst())
        })

        if (!missed.isEmpty()) {
            return DataResult.<JsonElement>error("some keys are not strings: $missed", output)
        }

        return DataResult.<JsonElement>success(output)
    }

    @Override
    DataResult<MapLike<JsonElement>> getMap(final JsonElement input) {
        if (!(input instanceof JsonObject)) {
            return DataResult.error("Not a JSON object: $input")
        }
        var object = (JsonObject) input
        return DataResult.<MapLike<JsonElement>>success(new MapLike<JsonElement>() {
            @Nullable
            @Override
            JsonElement get(JsonElement key) {
                if (key instanceof JsonPrimitive && key.getValue() instanceof String)
                    return get((String)key.getValue())
                return null
            }

            @Nullable
            @Override
            JsonElement get(String key) {
                JsonElement element = object.get(key)
                if (element instanceof JsonNull) {
                    return null
                }
                return element
            }

            @Override
            Stream<Pair<JsonElement, JsonElement>> entries() {
                return object.entrySet().stream().map {e -> Pair.<JsonElement, JsonElement>of(new JsonPrimitive(e.key), e.value)}
            }

            @Override
            String toString() {
                return "MapLike[$object]"
            }
        });
    }

    @Override
    DataResult<Stream<Pair<JsonElement, JsonElement>>> getMapValues(JsonElement input) {
        if (input instanceof JsonObject)
            return DataResult.<Stream<Pair<JsonElement,JsonElement>>>success(input.entrySet().stream().<Pair<JsonElement,JsonElement>>map {entry -> Pair.of(new JsonPrimitive(entry.key), entry.value instanceof JsonNull ? null : entry.value) })
        return DataResult.error("Not a JSON object: $input")
    }

    @Override
    JsonElement createMap(Stream<Pair<JsonElement, JsonElement>> map) {
        JsonObject result = new JsonObject();
        map.forEach(p -> {
            if (!(p.getFirst() instanceof JsonPrimitive && ((JsonPrimitive)p.getFirst()).getValue() instanceof String))
                throw new UnsupportedOperationException(p.getFirst().getClass().getSimpleName())
            result.put((String)((JsonPrimitive)p.getFirst()).getValue(), p.getSecond())
        })
        return result
    }

    @Override
    DataResult<Stream<JsonElement>> getStream(JsonElement input) {
        if (input instanceof JsonArray) {
            return DataResult.success(input.stream().map {e -> e instanceof JsonNull ? null : e })
        }
        return DataResult.error("Not a json array: " + input)
    }

    @Override
    JsonElement createList(Stream<JsonElement> input) {
        JsonArray result = new JsonArray()
        input.forEach(value -> result.add(value))
        return result
    }

    @Override
    JsonElement remove(JsonElement input, String key) {
        if (input instanceof JsonObject) {
            JsonObject result = new JsonObject()
            result.putAll(input)
            result.remove(key)
            return result
        }
        return input
    }

    String toString() {
        return "Jankson"
    }

    @Override
    JsonElement finalize(JsonElement input, CommentSpec spec) {
        if (commented && input instanceof JsonObject) {
            JsonObject copy = new JsonObject()
            copy.putAll(input)
            input.keySet().each {
                if (spec.getComment(it) !== null)
                    copy.setComment(it, spec.getComment(it))
            }
            return copy
        }
        return input
    }

    @Override
    DynamicOps<JsonElement> withoutComments() {
        return UNCOMMENTED
    }
}
