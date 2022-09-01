package io.github.groovymc.cgl.transform

import groovy.transform.CompileStatic
import groovy.transform.Generated
import groovy.transform.NamedParam
import groovy.transform.NamedVariant
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.tools.GeneralUtils

import javax.annotation.Nullable

import static org.objectweb.asm.Opcodes.*

@CompileStatic
class TransformUtils {
    static final AnnotationNode GENERATED_ANNOTATION = new AnnotationNode(ClassHelper.make(Generated))

    static final int CONSTANT_MODIFIERS = ACC_PRIVATE | ACC_STATIC | ACC_FINAL

    @NamedVariant
    static MethodNode addStaticMethod(@NamedParam(required = true) final ClassNode targetClassNode,
                                      @NamedParam(required = true) final String methodName,
                                      @NamedParam final int modifiers = ACC_PUBLIC | ACC_STATIC,
                                      @NamedParam final ClassNode returnType = ClassHelper.VOID_TYPE,
                                      @NamedParam final Parameter[] parameters = Parameter.EMPTY_ARRAY,
                                      @NamedParam final ClassNode[] exceptions = ClassNode.EMPTY_ARRAY,
                                      @NamedParam final List<AnnotationNode> annotations = [GENERATED_ANNOTATION],
                                      @NamedParam final Statement code = new BlockStatement(),
                                      @NamedParam @Nullable final Closure<Statement> conditionalCode = null) {

        addMethod(targetClassNode, methodName, modifiers, returnType, parameters, exceptions, annotations, code, conditionalCode)
    }

    @NamedVariant
    static MethodNode addMethod(@NamedParam(required = true) final ClassNode targetClassNode,
                                @NamedParam(required = true) final String methodName,
                                @NamedParam final int modifiers = ACC_PUBLIC,
                                @NamedParam final ClassNode returnType = ClassHelper.VOID_TYPE,
                                @NamedParam final Parameter[] parameters = Parameter.EMPTY_ARRAY,
                                @NamedParam final ClassNode[] exceptions = ClassNode.EMPTY_ARRAY,
                                @NamedParam final List<AnnotationNode> annotations = [GENERATED_ANNOTATION],
                                @NamedParam final Statement code = new BlockStatement(),
                                @NamedParam @Nullable final Closure<Statement> conditionalCode = null) {
        if (annotations) {
            final MethodNode maybeExistingMethod = targetClassNode.getDeclaredMethod(methodName, parameters)
            if (maybeExistingMethod !== null) return maybeExistingMethod

            final MethodNode method = new MethodNode(methodName, modifiers, returnType, parameters, exceptions, conditionalCode?.call() ?: code)
            method.addAnnotations(annotations)
            targetClassNode.addMethod(method)

            return method
        } else {
            return targetClassNode.addMethod(methodName, modifiers, returnType, parameters, exceptions, conditionalCode?.call() ?: code)
        }
    }

    @NamedVariant
    static FieldNode addField(@NamedParam(required = true) final ClassNode targetClassNode,
                              @NamedParam(required = true) final String fieldName,
                              @NamedParam final int modifiers = ACC_PUBLIC, // note: public fields aren't always properties
                              @NamedParam final ClassNode type = ClassHelper.OBJECT_TYPE,
                              @NamedParam @Nullable final Closure<ClassNode> conditionalType = null,
                              @NamedParam final List<AnnotationNode> annotations = [GENERATED_ANNOTATION],
                              @NamedParam final Expression initialValue = null,
                              @NamedParam @Nullable final Closure<Expression> conditionalInitialValue = null,
                              @NamedParam final boolean addFieldFirst = false) {
        if (annotations) {
            final FieldNode field = new FieldNode(fieldName, modifiers, conditionalType?.call() ?: type, targetClassNode, conditionalInitialValue?.call() ?: initialValue)
            field.addAnnotations(annotations)

            if (addFieldFirst) targetClassNode.addFieldFirst(field)
            else targetClassNode.addField(field)

            return field
        } else {
            if (addFieldFirst) return targetClassNode.addFieldFirst(fieldName, modifiers, conditionalType?.call() ?: type, conditionalInitialValue?.call() ?: initialValue)
            else return targetClassNode.addField(fieldName, modifiers, conditionalType?.call() ?: type, conditionalInitialValue?.call() ?: initialValue)
        }
    }

    static boolean isSubclass(final ClassNode target, final ClassNode superType) {
        if (superType.isInterface()) {
            return GeneralUtils.isOrImplements(target, superType)
        } else {
            return target.isDerivedFrom(superType)
        }
    }

    static ConstructorNode getOrCreatorCtor(final ClassNode classNode) {
        return classNode.declaredConstructors.find {
            !it.firstStatementIsSpecialConstructorCall()
        } ?: classNode.addConstructor(ACC_PUBLIC, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, new BlockStatement())
    }

    static void addLastCtorStatement(final ClassNode classNode, final Statement statement) {
        (getOrCreatorCtor(classNode).code as BlockStatement).addStatement(statement)
    }
}
