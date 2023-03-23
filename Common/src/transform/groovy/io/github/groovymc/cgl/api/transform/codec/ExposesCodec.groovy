/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.transform.codec

import groovy.transform.CompileStatic
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Marks a class as having a codec that can be used with members of that class.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@GroovyASTTransformationClass('io.github.groovymc.cgl.impl.transform.codec.ExposesCodecChecker')
@CompileStatic
@interface ExposesCodec {
    /**
     * The name of the property at which the codec is stored. If the annotated class is {@code A}, the value should
     * point to a property of type {@code Codec<A>}.
     */
    String value()
}