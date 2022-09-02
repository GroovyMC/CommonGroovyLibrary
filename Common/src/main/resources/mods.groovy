/*
 * Copyright (C) 2022 GroovyMC
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

ModsDotGroovy.make {
    modLoader = 'gml'
    loaderVersion = '[1,)'

    license = 'LGPL-3.0-or-later'
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
                    value = 'io.github.groovymc.cgl.quilt.CGLQuilt'
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
