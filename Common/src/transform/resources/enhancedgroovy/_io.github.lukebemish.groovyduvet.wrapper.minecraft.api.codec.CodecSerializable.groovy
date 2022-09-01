//file:noinspection GrPackage
import com.matyrobbrt.enhancedgroovy.dsl.ClassTransformer

((ClassTransformer) this.transformer).tap {
    it.addField([
            'name': 'CODEC',
            'type': "com.mojang.serialization.Codec<${it.className}>",
            'modifiers': ['public', 'static', 'final']
    ])
}
