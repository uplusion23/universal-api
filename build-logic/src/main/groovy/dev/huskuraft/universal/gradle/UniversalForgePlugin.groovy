package dev.huskuraft.universal.gradle

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

class UniversalForgePlugin implements Plugin<Project> {
    void apply(Project project) {

        project.pluginManager.apply('net.minecraftforge.gradle')

        def dataVersion = UniversalLibraryPlugin.getVersionCode(project)

        project.dependencies {
            minecraft "net.minecraftforge:forge:${project.minecraft_version}-${project.forge_version}"
            implementation('net.sf.jopt-simple:jopt-simple:5.0.4') { version { strictly '5.0.4' } }
        }

        project.minecraft {
            mappings channel: project.mapping_channel, version: project.mapping_version
            accessTransformer = project.file('src/main/resources/META-INF/accesstransformer.cfg')
            copyIdeResources = true
            runs {
                configureEach {
                    workingDirectory project.file('run')
                    property 'forge.logging.markers', 'REGISTRIES'
                    property 'forge.logging.console.level', 'debug'
                }

                client {
                    property 'forge.enabledGameTestNamespaces', project.mod_id
                    mods {
                        "${project.mod_id}" {
                            source project.sourceSets.main
//                        source project(":common-api").sourceSets.main
                        }
                    }
                }

                server {
                    property 'forge.enabledGameTestNamespaces', project.mod_id
                    args '--nogui'
                    mods {
                        "${project.mod_id}" {
                            source project.sourceSets.main
//                        source project(":common-api").sourceSets.main
                        }
                    }
                }

                gameTestServer {
                    property 'forge.enabledGameTestNamespaces', project.mod_id
                }
            }
        }

        // Forge uses the official mappings at runtime from 1.20.6. ForgeGradle does not reobfuscate these versions.
        if (dataVersion < 3801) {
            project.jar {
                finalizedBy 'reobfJar'
            }

            project.shadowJar {
                dependsOn 'reobfJar'
                project.reobf {
                    shadowJar {
                    }
                }
                finalizedBy 'reobfShadowJar'
            }
        }

    }

}
