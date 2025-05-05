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
    implementation("org.eclipse.jgit:org.eclipse.jgit:7.1.0.202411261347-r")

    implementation("com.google.code.gson:gson:2.13.1")

    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.26.3")
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

gradlePlugin {
    plugins {
        create("classifiedJars") {
            id = "dev.morazzer.cookies.internal.classified-jars"
            implementationClass = "cm.tasks.ModifyJarName"
        }
    }
}

afterEvaluate {
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(20)
    }
}
