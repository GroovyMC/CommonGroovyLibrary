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

package io.github.groovymc.cgl.transform.registroid.recipetype

import io.github.groovymc.cgl.transform.registroid.RegistroidAddonClass
import groovy.transform.CompileStatic

import java.lang.annotation.*

/**
 * An {@link io.github.groovymc.cgl.transform.registroid.RegistroidAddon Registroid addon} which makes
 * all {@linkplain net.minecraft.world.item.crafting.RecipeType RecipeType} fields within the class be created using
 * {@linkplain io.github.groovymc.cgl.extension.StaticGeneralExtensions#simple(net.minecraft.resources.ResourceLocation)}, with the
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