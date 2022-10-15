package io.github.groovymc.cgl.quilt.transform.environment

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.transform.environment.ExistsOnProcessor
import io.github.groovymc.cgl.transform.environment.Platform
import io.github.groovymc.cgl.transform.environment.Side
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.jetbrains.annotations.ApiStatus

import static org.codehaus.groovy.ast.ClassHelper.makeWithoutCaching

@CompileStatic
@ApiStatus.Internal
class ExistsOnProcessorImpl implements ExistsOnProcessor {
    static final ClassNode ENVIRONMENT = makeWithoutCaching(Environment)
    static final ClassNode ENV_TYPE = makeWithoutCaching(EnvType)

    @Override
    void process(AnnotatedNode node, Side side, List<Platform> platforms) {
        if (platforms.contains(Platform.QUILT)) {
            switch (side) {
                case Side.CLIENT:
                    node.addAnnotation(new AnnotationNode(ENVIRONMENT).tap {
                        it.addMember('value', new PropertyExpression(
                                new ClassExpression(ENV_TYPE),
                                new ConstantExpression(EnvType.CLIENT)))
                    })
                    break
                case Side.SERVER:
                    node.addAnnotation(new AnnotationNode(ENVIRONMENT).tap {
                        it.addMember('value', new PropertyExpression(
                                new ClassExpression(ENV_TYPE),
                                new ConstantExpression(EnvType.SERVER)))
                    })
                    break
            }
        }
    }
}
