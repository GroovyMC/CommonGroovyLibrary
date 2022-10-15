package io.github.groovymc.cgl.forge

import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.environment.Environment
import io.github.groovymc.cgl.api.environment.Platform
import io.github.groovymc.cgl.api.environment.Side
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.loading.FMLLoader
import org.jetbrains.annotations.ApiStatus

@CompileStatic
@ApiStatus.Internal
class EnvironmentImpl implements Environment {
    @Override
    Platform getPlatform() {
        Platform.FORGE
    }

    @Override
    Side getSide() {
        return switch (FMLLoader.dist) {
            case Dist.CLIENT -> Side.CLIENT
            case Dist.DEDICATED_SERVER -> Side.SERVER
        }
    }

    @Override
    boolean isProduction() {
        return FMLLoader.production
    }
}
