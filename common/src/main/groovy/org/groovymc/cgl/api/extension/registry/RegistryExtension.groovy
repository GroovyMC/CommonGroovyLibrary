package org.groovymc.cgl.api.extension.registry

import groovy.transform.CompileStatic
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey

/**
 * Extensions for working with registries in a map-like style.
 * @author CommonGroovyLibrary
 */
@CompileStatic
class RegistryExtension {
    static <A> A getAt(Registry<A> self, ResourceLocation name) {
        return self.get(name)
    }

    static <A> A getAt(Registry<A> self, ResourceKey<A> key) {
        return self.get(key)
    }

    static <A> void putAt(Registry<A> self, ResourceLocation name, A value) {
        Registry.register(self, name, value)
    }

    static <A> void putAt(Registry<A> self, ResourceKey<A> key, A value) {
        Registry.register(self, key, value)
    }

    static <A> ResourceLocation getAt(Registry<A> self, A member) {
        return self.getKey(member)
    }

    static <T> HolderSet.Named<T> getAt(Registry<T> self, TagKey<T> tag) {
        return self.getOrCreateTag(tag)
    }
}
