/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.impl.transform.registroid

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.transform.registroid.RegistroidAddon
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.tools.GeneralUtils

import java.util.function.Supplier

@CompileStatic
class SoundEventAddonTransformer implements RegistroidAddon {
    private static final ClassNode SOUND_EVENT_TYPE = ClassHelper.make(SoundEvent)
    private static final ClassNode RL_TYPE = ClassHelper.make(ResourceLocation)

    @Override
    void process(AnnotationNode registroidAnnotation, ClassNode targetClass, PropertyNode property, RegistroidASTTransformer transformer, Supplier<String> modId) {
        if (property.field.initialValueExpression === null) {
            property.field.setInitialValueExpression(
                    GeneralUtils.ctorX(SOUND_EVENT_TYPE, GeneralUtils.ctorX(
                            RL_TYPE, GeneralUtils.args(
                            GeneralUtils.constX(modId.get()), GeneralUtils.constX(transformer.getRegName(property))
                        )
                    ))
            )
        } else if (property.field.initialValueExpression instanceof ConstructorCallExpression) {
            final ctorExpr = ((ConstructorCallExpression) property.field.initialValueExpression)
            final args = ctorExpr.arguments
            if (args instanceof ArgumentListExpression && args.size() >= 1) {
                final arg0 = args[0]
                if (arg0 instanceof ConstantExpression && arg0.value === null) {
                    args.expressions.set(0, GeneralUtils.ctorX(
                            RL_TYPE, GeneralUtils.args(
                            GeneralUtils.constX(modId.get()), GeneralUtils.constX(transformer.getRegName(property))
                        )
                    ))
                }
            }
        }
    }

    @Override
    List<ClassNode> getSupportedTypes() {
        return [SOUND_EVENT_TYPE]
    }

    @Override
    List<PropertyExpression> getRequiredRegistries() {
        return [registryKeyProperty('SOUND_EVENT')]
    }
}
