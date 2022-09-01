/*
 * Copyright (C) 2022 GroovyMC
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package io.github.groovymc.cgl.transform.registroid.sound

import io.github.groovymc.cgl.transform.registroid.RegistroidASTTransformer
import io.github.groovymc.cgl.transform.registroid.RegistroidAddon
import groovy.transform.CompileStatic
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
