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

package io.github.groovymc.cgl.transform.registroid.blockitem

import io.github.groovymc.cgl.transform.registroid.RegistroidAddonClass
import groovy.transform.CompileStatic

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * An {@link io.github.groovymc.cgl.transform.registroid.RegistroidAddon Registroid addon} which creates {@link net.minecraft.world.item.BlockItem block items} automatically for blocks. <br>
 * This annotation can be used in field mode, or in class mode. <strong>Important:</strong> in order for field mode to work, the annotation needs to be applied on the Registroid class! <br> <br>
 * In class mode, all fields of type {@link net.minecraft.world.level.block.Block} in the Registroid class will have a {@link net.minecraft.world.item.BlockItem} generated using the {@link BlockItemAddon#value() factory}, unless explicitly specified otherwise. (using {@link BlockItemAddon#exclude()}) <br>
 * In field mode, it can be applied to any field of type {@link net.minecraft.world.level.block.Block} in a Registroid class. In the annotation you can either specify a custom {@link BlockItemAddon#value() factory} for that specific block, or you can {@link BlockItemAddon#exclude() exclude} the block from block item generation altogether.
 */
@Documented
@CompileStatic
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.TYPE, ElementType.FIELD])
@RegistroidAddonClass(BlockItemAddonTransformer)
@interface BlockItemAddon {
    /**
     * The block item factory, as a closure.
     * There are multiple ways of declaring the factory:
     * <ul>
     *     <li>a no-argument closure that returns a {@link net.minecraft.world.item.Item.Properties} which will be used along with a normal {@link net.minecraft.world.item.BlockItem}</li>
     *     <li>a closure accepting one argument, an instance of {@link net.minecraft.world.level.block.Block}, returning an object of type {@link net.minecraft.world.item.BlockItem}</li>
     * </ul>
     */
    Class<? extends Closure> value() default { null }

    /**
     * Only useful for us on fields. <br>
     * When {@code true} and applied to a field of type {@link net.minecraft.world.level.block.Block}
     * in a Registroid class, the block will be ignored from block item creation.
     */
    boolean exclude() default false
}