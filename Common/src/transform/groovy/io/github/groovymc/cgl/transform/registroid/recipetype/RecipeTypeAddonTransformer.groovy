/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.transform.registroid.recipetype

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.transform.registroid.RegistroidASTTransformer
import io.github.groovymc.cgl.transform.registroid.RegistroidAddon
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.RecipeType
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.tools.GeneralUtils
import org.jetbrains.annotations.ApiStatus

import java.util.function.Supplier

@CompileStatic
@ApiStatus.Internal
class RecipeTypeAddonTransformer implements RegistroidAddon {
    private static final ClassNode RECIPE_TYPE_TYPE = ClassHelper.make(RecipeType)
    private static final ClassNode RL_TYPE = ClassHelper.make(ResourceLocation)
    @Override
    void process(AnnotationNode registroidAnnotation, ClassNode targetClass, PropertyNode property, RegistroidASTTransformer transformer, Supplier<String> modId) {
        property.field.setInitialValueExpression(
                GeneralUtils.callX(RECIPE_TYPE_TYPE, 'simple', GeneralUtils.ctorX(
                        RL_TYPE, GeneralUtils.args(
                        GeneralUtils.constX(modId.get()), GeneralUtils.constX(transformer.getRegName(property))
                    )
                ))
        )
    }

    @Override
    List<ClassNode> getSupportedTypes() {
        return [RECIPE_TYPE_TYPE]
    }

    @Override
    List<PropertyExpression> getRequiredRegistries() {
        return [registryKeyProperty('RECIPE_TYPE')]
    }
}
