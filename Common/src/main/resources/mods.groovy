ModsDotGroovy.make {
    modLoader = 'gml'
    loaderVersion = '[1,)'

    license = 'MIT'
    issueTrackerUrl = 'https://github.com/GroovyMC/CommonGroovyLibrary/issues'

    mod {
        modId = this.buildProperties['mod_id']
        displayName = 'Common Groovy Library'
        version = this.version
        group = this.group
        intermediate_mappings = 'net.fabricmc:intermediary'
        displayUrl = 'https://github.com/GroovyMC/CommonGroovyLibrary'

        description = "A library for common easy Groovy mod development."
        authors = ['GroovyMC']

        dependencies {
            minecraft = this.minecraftVersionRange
            forge {
                versionRange = "[${this.forgeVersion},)"
            }
            quiltLoader {
                versionRange = ">=${this.quiltLoaderVersion}"
            }
        }

        entrypoints {
            init = [
                adapted {
                    adapter = 'groovyduvet'
                    value = 'io.github.groovymc.cgl.quilt.CGLQuilt'
                }
            ]
        }
    }
}