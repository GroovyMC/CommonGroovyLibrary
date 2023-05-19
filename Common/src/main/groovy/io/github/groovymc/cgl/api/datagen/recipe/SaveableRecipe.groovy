/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.datagen.recipe

import groovy.transform.CompileStatic
import net.minecraft.resources.ResourceLocation

@CompileStatic
trait SaveableRecipe {
    GRecipeProvider provider

    /**
     * Saves this recipe to the given {@code location}, using the provider's {@link GRecipeProvider#defaultNamespace  default namespace}.
     */
    void save(String location) {
        this.save(location.contains(':') ? new ResourceLocation(location) : new ResourceLocation(provider.defaultNamespace, location))
    }

    /**
     * Saves this recipe to the given {@code location}.
     */
    abstract void save(ResourceLocation location)
}