package io.github.groovymc.cgl.impl.transform.codec

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.transform.codec.ExposeCodecFactory
import io.github.groovymc.cgl.api.transform.codec.ExposesCodecFactory
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.TransformWithPriority
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport

import static org.codehaus.groovy.ast.ClassHelper.makeWithoutCaching

@CompileStatic
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
class ExposeCodecFactoryTransformation extends AbstractASTTransformation implements TransformWithPriority {

    static final ClassNode MY_TYPE = makeWithoutCaching(ExposeCodecFactory)
    static final ClassNode TARGET_TYPE = makeWithoutCaching(ExposesCodecFactory)

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        init(nodes, source)
        AnnotatedNode parent = (AnnotatedNode) nodes[1]
        AnnotationNode anno = (AnnotationNode) nodes[0]
        if (MY_TYPE != anno.getClassNode()) return

        if (parent instanceof MethodNode) {
            if (!parent.static) {
                throw new RuntimeException('ExposeCodecFactory may only expose static properties!')
            }
            if (parent.parameters.size() == 0) {
                throw new RuntimeException("ExposeCodecFactory can only be used to expose factory methods which construct new codecs. Currently applied to the method $parent.name, which takes no arguments.")
            }
            ClassNode clazz = parent.declaringClass
            ClassNode type = parent.returnType
            doApply(clazz, type, parent.name)
        }
    }

    static void doApply(ClassNode parent, ClassNode type, String name) {
        int parameterCount = parent.genericsTypes.size()
        if (parameterCount == 0) {
            throw new RuntimeException("Cannot use ExposeCodecFactory to expose a property when the parent class has no type parameters!")
        }
        if (parameterCount != type.genericsTypes.size()) {
            throw new RuntimeException("Cannot use ExposeCodecFactory to expose a property when the parent class has a different number of type parameters than the exposed codec!")
        }
        if (parent.annotations*.classNode.find {it == TARGET_TYPE}) {
            throw new RuntimeException("Cannot use ExposeCodecFactory to expose a property when the parent class already has a codec exposed!")
        }

        var parameterizedCodec = makeWithoutCaching('com.mojang.serialization.Codec')
        parameterizedCodec.setGenericsTypes(new GenericsType[]{new GenericsType(parent)})
        if (!StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(type, parameterizedCodec)) {
            throw new RuntimeException("ExposeCodecFactory used in ${parent.name} must annotate a method returning a properly parameterized codec. Current detected type is ${type.toString(false)}")
        }

        parent.addAnnotation(new AnnotationNode(TARGET_TYPE).tap {
            addMember('value', new ConstantExpression(name))
            addMember('parameters', new ConstantExpression(parameterCount))
        })
    }

    @Override
    int priority() {
        return 0
    }
}
