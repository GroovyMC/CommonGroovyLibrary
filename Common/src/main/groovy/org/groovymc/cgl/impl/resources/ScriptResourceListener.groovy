package org.groovymc.cgl.impl.resources

import groovy.transform.CompileStatic
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import org.codehaus.groovy.control.CompilerConfiguration

@CompileStatic
class ScriptResourceListener extends SimplePreparableReloadListener<Map<ResourceLocation, Map<ResourceLocation, Closure>>> {
    public static final Map<ResourceLocation, Map<ResourceLocation, Closure>> SCRIPTS = [:]
    public static final Map<ResourceLocation, Class<?>> PREFIXES = [:]

    public static final ResourceCompilerCustomizer CUSTOMIZER = ServiceLoader.load(ResourceCompilerCustomizer.class, ScriptResourceListener.classLoader)
            .findFirst().orElseThrow {
        new NullPointerException("Failed to load compiler customizer service for platform")
    }

    public static final CompilerConfiguration COMPILER_CONFIGURATION = new CompilerConfiguration().tap {
        CUSTOMIZER.customize(it)
    }
    public static final GroovyShell SHELL = new GroovyShell(ScriptResourceListener.classLoader, COMPILER_CONFIGURATION)

    @Override
    protected Map<ResourceLocation, Map<ResourceLocation, Closure>> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        SCRIPTS.clear()
        PREFIXES.each { prefix, contextType ->
            FileToIdConverter fileToIdConverter = new FileToIdConverter("$prefix.namespace/$prefix.path", ".groovy")

            fileToIdConverter.listMatchingResources(resourceManager).each { fileLocation, resource ->
                var idLocation = fileToIdConverter.fileToId(fileLocation)

                try (Reader reader = resource.openAsReader()) {
                    Closure closure = SHELL.evaluate("""
{ context ->
${reader.text}
}
""") as Closure
                    SCRIPTS.computeIfAbsent(prefix, { [:] }).put(idLocation, closure)
                }
            }
        }

        return SCRIPTS
    }

    @Override
    protected void apply(Map<ResourceLocation, Map<ResourceLocation, Closure>> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        // We've already provessed scripts
    }
}