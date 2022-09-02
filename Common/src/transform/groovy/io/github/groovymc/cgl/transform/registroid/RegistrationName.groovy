/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.transform.registroid

import groovy.transform.CompileStatic

import java.lang.annotation.*

/**
 * Annotated used to change how the registry name of an object in a {@linkplain io.github.groovymc.cgl.transform.Registroid Registroid context} is determined.
 * <h2>How registry names are determined</h2>
 * Usually, the registry name of a field is deduced as {@code fieldName.toLowerCase()}. <br>
 * If the field is annotated with {@linkplain io.github.groovymc.cgl.transform.registroid.RegistrationName RegistrationName}, the specified {@linkplain io.github.groovymc.cgl.transform.registroid.RegistrationName#value() name} will be used. <br>
 * If the field is in a class marked with {@linkplain io.github.groovymc.cgl.transform.registroid.RegistrationName RegistrationName}, there are options ways the name may be deduced:
 * <ul>
 *  <li>If the field is not annotated with {@linkplain io.github.groovymc.cgl.transform.registroid.RegistrationName RegistrationName}, the {@code prefix + fieldName.toLowerCase()} formula will be
 *  used, where prefix is specified in the {@linkplain io.github.groovymc.cgl.transform.registroid.RegistrationName#value() class annotation}</li>
 *  <li>If the field is annotated with {@linkplain io.github.groovymc.cgl.transform.registroid.RegistrationName RegistrationName}, there are two options:
 *  <ul>
 *      <li>If {@linkplain io.github.groovymc.cgl.transform.registroid.RegistrationName#alwaysApply() prefixes are forced}, the registry name will be {@code prefix + annotation.value}</li>
 *      <li>Otherwise, the usual {@code annotation.value} formula will be used
 *  </ul>
 * </ul>
 */
@Documented
@CompileStatic
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.FIELD, ElementType.TYPE])
@interface RegistrationName {
    /**
     * When applied on a field, this property can be used to give a field a custom name. <br>
     * When applied on a class, this value will be appended as a prefix to fields inside it, as per the docs of {@link RegistrationName}.
     */
    String value()

    /**
     * Only used by the inner class mode. <br>
     * If the prefix should be applied even when fields explicitly declare a {@linkplain RegistrationName#value() name}. <br>
     * Defaults to false.
     */
    boolean alwaysApply() default false
}