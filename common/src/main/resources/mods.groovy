MultiplatformModsDotGroovy.make {
    modLoader = 'gml'
    loaderVersion = '[1,)'

    license = 'LGPL-3.0-or-later'
    issueTrackerUrl = 'https://github.com/GroovyMC/CommonGroovyLibrary/issues'

    mod {
        modId = buildProperties.mod_id
        displayName = buildProperties.mod_name
        version = environmentInfo.version

        displayUrl = 'https://github.com/GroovyMC/CommonGroovyLibrary'

        description = "A library for common easy Groovy mod development."
        author = buildProperties.mod_author

        dependencies {
            minecraft = ">=${environmentInfo.minecraftVersion}"

            onNeoForge {
                mod('neoforge') {
                    versionRange = ">=${environmentInfo.platformVersion}"
                }
                mod('gml') {
                    versionRange = ">=${libs.versions.gml}"
                }
            }

            onFabric {
                mod('groovyduvet_core') {
                    versionRange = ">=${libs.versions.groovyduvet}"
                }
                mod('fabricloader') {
                    versionRange = ">=${environmentInfo.platformVersion}"
                }
                mod('fabric-api') {
                    versionRange = ">=${libs.versions.fabric_api}"
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
