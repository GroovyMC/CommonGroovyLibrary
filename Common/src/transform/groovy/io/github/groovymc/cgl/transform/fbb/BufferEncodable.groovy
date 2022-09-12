package io.github.groovymc.cgl.transform.fbb

import groovy.transform.CompileStatic
import org.codehaus.groovy.transform.GroovyASTTransformationClass
import org.jetbrains.annotations.ApiStatus

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@CompileStatic
@ApiStatus.Experimental
@Target(ElementType.TYPE)
@Deprecated(since = 'Not usable yet!')
@Retention(RetentionPolicy.SOURCE)
@GroovyASTTransformationClass('io.github.groovymc.cgl.transform.fbb.BufferEncodableASTTransformer')
@interface BufferEncodable {

}