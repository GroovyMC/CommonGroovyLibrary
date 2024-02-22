package org.groovymc.cgl.impl.transform.codec

import groovy.transform.CompileStatic
import org.groovymc.cgl.api.transform.codec.ExposesCodecFactory
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.TransformWithPriority
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport

import static org.codehaus.groovy.ast.ClassHelper.makeWithoutCaching

@CompileStatic
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
class ExposesCodecFactoryChecker extends AbstractASTTransformation implements TransformWithPriority {

    static final ClassNode MY_TYPE = makeWithoutCaching(ExposesCodecFactory)
    static final ClassNode CODEC_TYPE = makeWithoutCaching('com.mojang.serialization.Codec')

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        init(nodes, source)
        AnnotatedNode parent = (AnnotatedNode) nodes[1]
        AnnotationNode anno = (AnnotationNode) nodes[0]
        if (MY_TYPE != anno.getClassNode()) return

        if (parent instanceof ClassNode) {
            String value = getMemberStringValue(anno, 'value', '')
            if (value.isEmpty()) {
                throw new RuntimeException('ExposesCodecFactory must have a value!')
            }
            List<MethodNode> methods = parent.methods.findAll {
                it.name == value && it.static && it.parameters.length == 0
            } ?: {
                throw new RuntimeException("ExposesCodecFactory value $value is not a static method of $parent.name!")
                return (List<MethodNode>)null
            }.call()

            methods.any {
                var parameterizedCodec = makeWithoutCaching('com.mojang.serialization.Codec')
                GenericsType[] genericsTypes = new GenericsType[it.parameters.length]
                for (int i = 0; i < genericsTypes.length; i++) {
                    var parameterType = it.parameters[i].type
                    if (!StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(parameterType, CODEC_TYPE) || parameterType.genericsTypes.length != 1) {
                        throw new RuntimeException("ExposesCodecFactory value $value does not expose a properly parameterized codec!")
                    }
                    genericsTypes[i] = new GenericsType(parameterType.genericsTypes[0].type)
                }
                parameterizedCodec.setGenericsTypes(genericsTypes)
                return StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(it.returnType, parameterizedCodec)
            } || {
                throw new RuntimeException("ExposesCodecFactory value $value does not expose a properly parameterized codec!")
                return false
            }.call()
        }
    }

    @Override
    int priority() {
        return -2
    }
}
