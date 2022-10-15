package io.github.groovymc.cgl.transform.environment

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.environment.Platform
import io.github.groovymc.cgl.api.environment.Side
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PACKAGE, ElementType.ANNOTATION_TYPE])
@GroovyASTTransformationClass('io.github.groovymc.cgl.transform.environment.ExistsOnASTTransformer')
@CompileStatic
@interface ExistsOn {
    Side value()

    Platform[] applyOn() default [Platform.FORGE, Platform.QUILT]
}