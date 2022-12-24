/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

ModsDotGroovy.make {
    modLoader = 'gml'
    loaderVersion = '[1,)'

    license = 'LGPL-3.0-or-later'
    issueTrackerUrl = 'https://github.com/GroovyMC/CommonGroovyLibrary/issues'

    mod {
        modId = this.buildProperties['mod_id']
        displayName = this.buildProperties['mod_name']
        version = this.version
        group = this.group
        intermediate_mappings = 'net.fabricmc:intermediary'
        displayUrl = 'https://github.com/GroovyMC/CommonGroovyLibrary'

        description = "A library for common easy Groovy mod development."
        authors = [this.buildProperties['mod_author']]

        dependencies {
            minecraft = this.buildProperties['minecraft_version']

            forge {
                versionRange = ">=${this.forgeVersion}"
            }
            onForge {
                mod('gml') {
                    versionRange = ">=${this.buildProperties['gml_version']}"
                }
            }

            onQuilt {
                mod('groovyduvet_core') {
                    versionRange = ">=${this.buildProperties['groovyduvet_version']}"
                }
            }

            quiltLoader {
                versionRange = ">=${this.quiltLoaderVersion}"
            }
        }

        entrypoints {
            init = [
                adapted {
                    adapter = 'groovyduvet'
                    value = 'io.github.groovymc.cgl.impl.quilt.CGLQuilt'
                }
            ]
        }
    }

    onQuilt {
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
