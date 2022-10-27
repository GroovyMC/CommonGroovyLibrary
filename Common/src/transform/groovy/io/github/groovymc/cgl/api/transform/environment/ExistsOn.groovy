/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

//file:noinspection GroovyDocCheck

package io.github.groovymc.cgl.api.transform.environment

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.environment.Platform
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
/**
 * An annotation that can be used to specify that a class, field, method, or constructor is only available on specific
 * loaders and/or sides. This annotation will be replaced with {@link org.quiltmc.loader.api.minecraft.ClientOnly} or
 * {@link org.quiltmc.loader.api.minecraft.DedicatedServerOnly} on Quilt, or
 * {@link net.minecraftforge.api.distmarker.OnlyIn} on Forge if the annotated element must be present on only one side.
 * <p>
 * <strong>Warning:</strong> the use of {@link net.minecraftforge.api.distmarker.OnlyIn} is generally considered bad
 * practice on forge. As such, this annotation should be used with care when affecting forge classes.
 * <p>
 * This annotation works reliably for fields, methods, constructors, and classes (though field initializers <strong>do
 * not</strong> get stripped); other annotation targets may find patchy or inconsistent support, due to limitations of
 * transformers and differing behavior between Quilt and Forge's annotation behavior.
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.TYPE, ElementType.TYPE_USE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PACKAGE, ElementType.ANNOTATION_TYPE])
@GroovyASTTransformationClass(['io.github.groovymc.cgl.impl.transform.environment.ExistsOnAnnotationTransformer','io.github.groovymc.cgl.impl.transform.environment.ExistsOnRemovalTransformer'])
@CompileStatic
@interface ExistsOn {
    /**
     * The sides and loaders to limit this element to. Will be removed entirely on non-listed loader-side combinations.
     */
    Platform[] value()
}