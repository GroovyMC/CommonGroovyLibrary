/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.cgl.api.transform.registroid

import groovy.transform.CompileStatic
import org.groovymc.cgl.impl.transform.registroid.RecipeTypeAddonTransformer

import java.lang.annotation.*

/**
 * An {@link RegistroidAddon Registroid addon} which makes
 * all {@linkplain net.minecraft.world.item.crafting.RecipeType RecipeType} fields within the class be created using
 * {@linkplain org.groovymc.cgl.api.extension.StaticGeneralExtensions#simple(net.minecraft.resources.ResourceLocation)}, with the
 * name being the registry name of the RecipeType field. You do <strong>not</strong> need to initialise the field. <br> <br>
 * Example in:
 * <pre>
 * {@code static final RecipeType<MyRecipe> HELLO_WORLD}
 * </pre>
 * Example out:
 * <pre>
 * {@code static final RecipeType<MyRecipe> HELLO_WORLD = RecipeType.simple(new ResourceLocation(yourModId, 'hello_world'))}
 * </pre>
 */
@Documented
@CompileStatic
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@RegistroidAddonClass(RecipeTypeAddonTransformer)
@interface RecipeTypeAddon {}