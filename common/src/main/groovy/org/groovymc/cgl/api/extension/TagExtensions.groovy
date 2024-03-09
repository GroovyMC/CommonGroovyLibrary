package org.groovymc.cgl.api.extension

import groovy.transform.CompileStatic
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.tags.TagKey

/**
 * Extensions for working with tags.
 * @author CommonGroovyLibrary
 */
@CompileStatic
class TagExtensions {
    static <T> boolean isCase(HolderSet.Named<T> holder, T object) {
        return holder.stream().map(Holder::value).anyMatch(it -> object === it)
    }

    static <T> HolderSet.Named<T> get(TagKey<T> tag, Registry<T> registry) {
        return registry.getOrCreateTag(tag)
    }
}
