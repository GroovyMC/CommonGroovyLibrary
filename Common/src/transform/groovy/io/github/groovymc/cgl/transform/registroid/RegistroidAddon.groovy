package io.github.groovymc.cgl.transform.registroid

import groovy.transform.CompileStatic
import net.minecraft.core.Registry
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.tools.GeneralUtils

import java.util.function.Supplier

/**
 * An addon for {@linkplain io.github.groovymc.cgl.transform.Registroid Registroid} systems. <br>
 * The addons are specified on classes annotated with {@linkplain io.github.groovymc.cgl.transform.Registroid Registroid},
 * by annotating the class with different {@linkplain RegistroidAddonClass addon annotations}.
 * <br>
 * Addons provided by Groovylicious:
 * <ul>
 *     <li>{@linkplain io.github.groovymc.cgl.transform.registroid.blockitem.BlockItemAddon BlockItem addon}</li>
 *     <li>{@linkplain io.github.groovymc.cgl.transform.registroid.recipetype.RecipeTypeAddon RecipeType addon}</li>
 *     <li>{@linkplain io.github.groovymc.cgl.transform.registroid.sound.SoundEventAddon SoundEvent addon}</li>
 * </ul>
 */
@CompileStatic
interface RegistroidAddon {
    ClassNode REGISTRY_TYPE = ClassHelper.make(Registry)

    /**
     * Processes a property of one of the {@linkplain #getSupportedTypes() supported types}.
     */
    void process(AnnotationNode registroidAnnotation, ClassNode targetClass, PropertyNode property, RegistroidASTTransformer transformer, Supplier<String> modId)

    /**
     * The types this addon can process. (e.g. {@linkplain net.minecraft.world.item.Item Item}, {@linkplain net.minecraft.world.level.block.Block block})
     */
    List<ClassNode> getSupportedTypes()

    /**
     * A list of property expressions representing the registries this addon needs in order to properly work.
     * @see io.github.groovymc.cgl.transform.Registroid#value()
     */
    List<PropertyExpression> getRequiredRegistries()

    /**
     * A helper that creates a property expression for a registry key in {@link Registry}, in the following way:
     * <pre>{@code GeneralUtils.propX(GeneralUtils.classX(REGISTRY_TYPE), "${name}_REGISTRY")}</pre>
     */
    default PropertyExpression registryKeyProperty(String name) {
        GeneralUtils.propX(GeneralUtils.classX(REGISTRY_TYPE), "${name}_REGISTRY")
    }
}