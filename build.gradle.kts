import org.gradle.jvm.tasks.Jar

plugins {
	id("fabric-loom") version "1.10-SNAPSHOT"
	id("maven-publish")
	id("dev.morazzer.cookies.internal.classified-jars")
}

version = project.properties["mod_version"]!!
group = project.properties["maven_group"]!!

repositories {
	mavenCentral()
	maven("https://api.modrinth.com/maven")
	maven("https://repo.hypixel.net/repository/Hypixel/")
	maven("https://repo.cookies.codes/releases")
	maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
	maven("https://maven.teamresourceful.com/repository/maven-public/")
}

fabricApi {
	configureDataGeneration {
		createSourceSet = true
	}
}

loom {
	accessWidenerPath.set(rootProject.rootDir.resolve("src/main/resources/cookies.accesswidener"))
}

val includeInJar: Configuration = configurations.create("includeInJar") {
	isCanBeResolved = true
}
configurations.implementation.get().extendsFrom(includeInJar)

dependencies {
	annotationProcessor(compileOnly("org.projectlombok:lombok:1.18.38")!!)
	compileOnly("org.jetbrains:annotations:26.0.1")

	includeInJar("dev.morazzer.cookies:entities:0.2.0")
	// Hypixel mod api fabric
	include(modImplementation("maven.modrinth:hypixel-mod-api:1.0.1+build.1+mc1.21") {
		isTransitive = false
	})
	implementation("net.hypixel:mod-api:1.0.1")

	minecraft("com.mojang:minecraft:${project.properties["minecraft_version"]}")
	mappings("net.fabricmc:yarn:${project.properties["yarn_mappings"]}:v2")

	modImplementation("net.fabricmc:fabric-loader:${project.properties["loader_version"]}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${project.properties["fabric_version"]}")
	modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:1.2.1")

	include(modImplementation("com.teamresourceful.resourcefulconfig:resourcefulconfig-fabric-${project.properties["minecraft_version"]}:${project.properties["rconfig_version"]}")!!)
}

tasks.withType<Jar>().configureEach {
	from(includeInJar.resolve().map { if (it.isDirectory) it else zipTree(it) })
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

tasks.register("generateBuildInfo", cm.tasks.CreateBuildInfo::class) {
	this.outputDir.set(this.project.layout.buildDirectory.dir("generated/buildinfo"))
}

tasks.register("generateRegions", cm.tasks.CreateRegions::class) {
	this.outputDir.set(this.project.layout.buildDirectory.dir("generated/regions"))
}

sourceSets {
	main {
		java {
			srcDir(tasks.getByName("generateDatacomponentAccessors"))
			srcDir(tasks.getByName("generateBuildInfo"))
			srcDir(tasks.getByName("generateRegions"))
		}
	}
}
