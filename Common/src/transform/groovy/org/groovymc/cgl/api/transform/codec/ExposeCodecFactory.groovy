/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.cgl.api.transform.codec

import groovy.transform.CompileStatic
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Exposes a single factory method as being able to produce a parameterized codec within a class. The transformation
 * gives the parent class the {@link ExposesCodecFactory} annotation, pointing at the method defined by the annotated
 * method or field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@GroovyASTTransformationClass('org.groovymc.cgl.impl.transform.codec.ExposeCodecFactoryTransformation')
@CompileStatic
@interface ExposeCodecFactory {

}