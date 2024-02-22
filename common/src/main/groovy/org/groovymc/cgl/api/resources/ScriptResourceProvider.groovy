package org.groovymc.cgl.api.resources

import com.mojang.datafixers.util.Pair
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import groovy.transform.VisibilityOptions
import groovy.transform.options.Visibility
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromAbstractTypeMethods
import net.minecraft.resources.ResourceLocation
import org.groovymc.cgl.impl.resources.ScriptResourceListener

@CompileStatic
@TupleConstructor(visibilityId = 'private')
@VisibilityOptions(id = 'private', value = Visibility.PRIVATE)
final class ScriptResourceProvider<T> {
    final ResourceLocation prefix
    final Class<? super T> clazz

    Closure getResource(ResourceLocation id) {
        return ScriptResourceListener.SCRIPTS.getOrDefault(prefix, new LinkedHashMap<ResourceLocation, Closure>()).get(id)
    }

    void getResources(@ClosureParams(value = FromAbstractTypeMethods, options = ['org.groovymc.cgl.impl.resources.ResourceConsumer']) Closure resourceConsumer) {
        ScriptResourceListener.SCRIPTS.getOrDefault(prefix, new LinkedHashMap<ResourceLocation, Closure>()).each { id, closure ->
            if (resourceConsumer.getMaximumNumberOfParameters() == 2) {
                resourceConsumer.call(id, closure)
            } else {
                resourceConsumer.call(new Pair<>(id, closure))
            }
        }
    }
}
