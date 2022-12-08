/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.transform.registroid

import groovy.transform.CompileStatic
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.*

/**
 * An annotation which transforms a class with static registrable object properties into a {@linkplain io.github.groovymc.cgl.reg.RegistrationProvider} + {@linkplain io.github.groovymc.cgl.reg.RegistryObject} + getters class. <br>
 * This annotation can be used in either field or class mode. <br><br>
 * <h2>Field mode</h2>
 * The annotation is applied on a {@linkplain io.github.groovymc.cgl.reg.RegistrationProvider RegistrationProvider} field. The transformer will scan all properties of the DeferredRegister's type in the field's class, and convert them to registry objects,
 * with getters calling {@linkplain io.github.groovymc.cgl.reg.RegistryObject#get()}.
 *
 * <h2>Class mode</h2>
 * In class mode, DR fields can be automatically created, by specifying the registries to create them for, in the {@linkplain Registroid#value() value} property.
 * The created DRs will behave like they do in field mode.<br>
 * Moreover, {@linkplain io.github.groovymc.cgl.api.transform.registroid.RegistroidAddon addons} can be added to the DRs in the class by annotation the class with the respective {@linkplain io.github.groovymc.cgl.api.transform.registroid.RegistroidAddonClass addon annotations}.
 *
 * The way registry name are determined can be found in the {@linkplain io.github.groovymc.cgl.api.transform.registroid.RegistrationName RegistrationName} documentation.
 */
@Documented
@CompileStatic
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.FIELD, ElementType.TYPE])
@GroovyASTTransformationClass(value = 'io.github.groovymc.cgl.impl.transform.registroid.RegistroidASTTransformer')
@interface Registroid {
    /**
     * If objects from inner classes should be transformed as well. <br>
     * Defaults to false.
     */
    boolean includeInnerClasses() default false

    /**
     * If the class holding this Registroid should get automatically classloaded in your mod's main class. <br>
     * Only works on Forge, or if a custom {@linkplain io.github.groovymc.cgl.api.transform.util.ModClassTransformer.Helper} is implemented for your mod ID.
     */
    boolean registerAutomatically() default true

    /**
     * Only used by class mode! <br>
     * In this property you specify the registries you want DRs to be automatically created, as a closure. <br>
     * The closure <b>must</b> constantly return one of the following types:
     * <ul>
     *     <li>{@linkplain net.minecraft.core.Registry Registry}</li>
     *     <li>{@linkplain net.minecraft.resources.ResourceKey ResourceKey}</li>
     *     <li>{@linkplain net.minecraftforge.registries.IForgeRegistry ForgeRegistry} (if on Forge)</li>
     *     <li>or a list (using the {@code [val1, val2] format}) of the above (can be empty, or having multiple accepted types combined)</li>
     * </ul>
     * This value is optional, and when not specified, it will behave like an empty list.
     */
    @SuppressWarnings('GroovyDocCheck')
    Class<? extends Closure> value() default {}

    /**
     * The modId to use for automatic {@linkplain io.github.groovymc.cgl.reg.RegistrationProvider} generation. <br>
     * On Forge, the GML API is used in order to determine the modId of the package if this property is not specified. <br>
     * On other platforms (Quilt / Common), specify the modId, or implement a custom {@linkplain io.github.groovymc.cgl.api.transform.util.ModIdRequester.Helper} using services, for your own package and modId.
     */
    String modId() default ''
}
