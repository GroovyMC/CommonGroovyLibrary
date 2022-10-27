/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.transform.registroid

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.impl.transform.registroid.BlockItemAddonTransformer

import java.lang.annotation.*

/**
 * An {@link RegistroidAddon Registroid addon} which creates {@link net.minecraft.world.item.BlockItem block items} automatically for blocks. <br>
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