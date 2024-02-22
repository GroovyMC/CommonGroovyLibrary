//file:noinspection GrPackage
import com.matyrobbrt.enhancedgroovy.dsl.ClassTransformer
import com.matyrobbrt.enhancedgroovy.dsl.members.Annotation

final transformer = ((ClassTransformer) this.transformer)
final annotation = ((Annotation) this.annotation)

transformer.addField([
        'name': annotation.getAttribute('property'),
        'type': "com.mojang.serialization.Codec<${transformer.className}>",
        'modifiers': ['public', 'static', 'final']
])
