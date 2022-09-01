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

package io.github.groovymc.cgl.transform.registroid.sound

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.transform.registroid.RegistroidAddonClass

import java.lang.annotation.*

/**
 * An {@link io.github.groovymc.cgl.transform.registroid.RegistroidAddon Registroid addon} which makes
 * all {@linkplain net.minecraft.sounds.SoundEvent SoundEvent} fields within the class be created using
 * the correct deduced registry name. You do <strong>not</strong> need to initialise the field. <br><br>
 * Example in:
 * <pre>
 * {@code static final SoundEvent HELLO_WORLD}
 * {@code static final SoundEvent HELLO_THERE = new SoundEvent(null, 0.12f)}
 * </pre>
 * Example out:
 * <pre>
 * {@code static final SoundEvent HELLO_WORLD = new SoundEvent(new ResourceLocation(yourModId, 'hello_world'))}
 * {@code static final SoundEvent HELLO_THERE = new SoundEvent(new ResourceLocation(yourModId, 'hello_there'), 0.12f)}
 * </pre>
 */
@Documented
@CompileStatic
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@RegistroidAddonClass(SoundEventAddonTransformer)
@interface SoundEventAddon {}