import org.gradle.jvm.tasks.Jar

plugins {
    id("fabric-loom")
    id("maven-publish")
}

version = project.properties["mod_version"]!!
group = project.properties["maven_group"]!!

repositories {
}

fabricApi {
    configureDataGeneration()
}

loom {
    accessWidenerPath.set(rootProject.rootDir.resolve("src/main/resources/cookies.accesswidener"))
}

dependencies {
    annotationProcessor(compileOnly("org.projectlombok:lombok:1.18.32")!!)
    compileOnly("org.jetbrains:annotations:1.18.32")

    minecraft("com.mojang:minecraft:${project.properties["minecraft_version"]}")
    mappings("net.fabricmc:yarn:${project.properties["yarn_mappings"]}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.properties["loader_version"]}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.properties["fabric_version"]}")

}

tasks.withType<ProcessResources> {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand("version" to version)
    }
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Jar> {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}

tasks.withType<JavaCompile> {
    options.release = 21
    sourceCompatibility = "21"
    targetCompatibility = "21"
}

tasks.register("generateDatacomponentAccessors", cm.tasks.GenerateDataComponentMethods::class) {
    this.outputDir.set(this.project.layout.buildDirectory.dir("generated/data-component-accessors"))
}

sourceSets {
    main {
        java {
            srcDir(tasks.getByName("generateDatacomponentAccessors"))
        }
    }
}