package org.groovymc.cgl.api.codec.comments

import groovy.transform.CompileStatic

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Mark a property of a {@link org.groovymc.cgl.api.transform.codec.CodecSerializable} class as
 * having a comment attached, and provide the comment. Should be placed on the field or getter method for the property.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.FIELD, ElementType.METHOD])
@CompileStatic
@interface Comment {
    /**
     * The comment to give the marked property. Will have whitespace trimmed.
     */
    String value()
}
