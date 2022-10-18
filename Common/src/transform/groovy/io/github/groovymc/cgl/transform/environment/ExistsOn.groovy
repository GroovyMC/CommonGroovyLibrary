/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

//file:noinspection GroovyDocCheck

package io.github.groovymc.cgl.transform.environment

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.environment.Loader
import io.github.groovymc.cgl.api.environment.Side
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * An annotation that can be used to specify that a class, field, method, or constructor is only available on a specific
 * loader. This annotation will be replaced with {@link org.quiltmc.loader.api.minecraft.ClientOnly} or
 * {@link org.quiltmc.loader.api.minecraft.DedicatedServerOnly} on Quilt, or
 * {@link net.minecraftforge.api.distmarker.OnlyIn} on Forge.
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.TYPE, ElementType.TYPE_USE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PACKAGE, ElementType.ANNOTATION_TYPE])
@GroovyASTTransformationClass('io.github.groovymc.cgl.transform.environment.ExistsOnASTTransformer')
@CompileStatic
@interface ExistsOn {
    /**
     * The physical side to limit this element to.
     */
    Side value()

    /**
     * The loaders on which to limit the element. The annotation is dropped entirely on loaders not listed.
     */
    Loader[] applyOn() default [Loader.FORGE, Loader.QUILT]
}