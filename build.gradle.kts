plugins {
    id("java")
    id("net.neoforged.moddev") version "2.0.+"
}

version = project.property("mod_version")!!
group = project.property("maven_group")!!

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

neoForge {
    version = "21.1.209"
}

repositories {
    maven("https://maven.neoforged.net/releases")
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}