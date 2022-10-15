package io.github.groovymc.cgl.transform.environment

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.jetbrains.annotations.ApiStatus

import java.util.function.Function

import static org.codehaus.groovy.ast.ClassHelper.makeWithoutCaching

@CompileStatic
@ApiStatus.Internal
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
class ExistsOnASTTransformer extends AbstractASTTransformation {
    static final ClassNode MY_TYPE = makeWithoutCaching(ExistsOn)

    static final ExistsOnProcessor PROCESSOR = ServiceLoader.load(ExistsOnProcessor)
            .findFirst()
            .orElseThrow({ -> new NullPointerException("Failed to load service for ${ExistsOnProcessor.name}") })

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        init(nodes, source)
        AnnotatedNode parent = (AnnotatedNode) nodes[1]
        AnnotationNode anno = (AnnotationNode) nodes[0]
        if (MY_TYPE != anno.getClassNode()) return

        Side side = parseSingleExpr<Side>(anno.getMember('value'), Side::valueOf)
        List<Platform> platforms = getMemberValues<Platform>(anno, 'platforms', Platform::valueOf)

        PROCESSOR.process(parent, side, platforms)
    }

    static <T extends Enum> List<T> getMemberValues(AnnotationNode anno, String name, Function<String,T> getter) {
        Expression expr = anno.getMember(name)
        if (expr == null) {
            return []
        }
        if (expr instanceof ListExpression) {
            final ListExpression listExpression = (ListExpression) expr
            List<T> list = new ArrayList<>();
            for (Expression itemExpr : listExpression.getExpressions()) {
                if (itemExpr instanceof ConstantExpression) {
                    T value = parseSingleExpr(itemExpr, getter)
                    if (value != null) list.add(value)
                }
            }
            return list
        }
        T single = parseSingleExpr(expr, getter)
        return single==null?([]):([single])
    }

    static <T extends Enum> T parseSingleExpr(Expression itemExpr, Function<String,T> getter) {
        if (itemExpr instanceof VariableExpression) {
            return getter.apply(itemExpr.text)
        } else if (itemExpr instanceof PropertyExpression) {
            return getter.apply(itemExpr.propertyAsString)
        }
        return null
    }
}
