MultiplatformModsDotGroovy.make {
    modLoader = 'gml'
    loaderVersion = '[1,)'

    license = 'LGPL-3.0-or-later'
    issueTrackerUrl = 'https://github.com/GroovyMC/CommonGroovyLibrary/issues'

    mod {
        modId = buildProperties['mod_id']
        displayName = buildProperties['mod_name']
        version = buildProperties['version']
        displayUrl = 'https://github.com/GroovyMC/CommonGroovyLibrary'

        description = "A library for common easy Groovy mod development."
        author = buildProperties['mod_author']

        dependencies {
            minecraft = ">=${buildProperties['minecraftVersion']}"

            onForge {
                mod('neoforge') {
                    versionRange = ">=${buildProperties['platformVersion']}"
                }
                mod('gml') {
                    versionRange = ">=${buildProperties['gml_version']}"
                }
            }

            onFabric {
                mod('groovyduvet_core') {
                    versionRange = ">=${buildProperties['groovyduvet_core_version']}"
                }
                mod('fabric_loader') {
                    versionRange = ">=${buildProperties['platformVersion']}"
                }
            }
        }

        entrypoints {
            entrypoint('main') {
                adapter = 'groovyduvet'
                value = 'org.groovymc.cgl.impl.fabric.CGLFabric'
            }
        }
    }

    onFabric {
        modmenu = [
            'badges':['library'],
            'parent':[
                'id':'groovyduvet',
                'name':'GroovyDuvet',
                'description':'Language adapter and wrapper libraries for Groovy mods on Quilt',
                'icon':'assets/groovyduvet/icon.png',
                'badges':['library']
            ]
        ]
    }
}
