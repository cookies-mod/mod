plugins {
    id("java")
    id("java-gradle-plugin")
    id("groovy-gradle-plugin")
}

repositories {
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.quiltmc.org/repository/release")
}

dependencies {
    implementation(project.libs.fabric.loom)
    implementation(project.libs.indra.git)
    //implementation(project.libs.quilt.parsers.json)
    //implementation(project.libs.yumi.gradle.licenser)

    implementation("com.google.code.gson:gson:2.11.0")

    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.25.10")
    implementation("com.github.johnrengelman:shadow:8.1.1")
}

tasks.withType<JavaCompile> {
    options.release = 21
    sourceCompatibility = "21"
    targetCompatibility = "21"
}

java {
    sourceCompatibility = JavaVersion.VERSION_20
    targetCompatibility = JavaVersion.VERSION_20
}


afterEvaluate {
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(20)
    }
}