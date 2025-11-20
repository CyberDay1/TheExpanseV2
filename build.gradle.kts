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

tasks.processResources {
    // Avoid overriding vanilla overworld density functions, which was causing
    // "Unbound values in registry minecraft:worldgen/density_function" when
    // opening the Singleplayer/Create World screen.
    //
    // We keep the source JSONs in the repo for reference, but they are
    // excluded from the built mod jar so vanilla definitions remain intact.
    exclude("data/minecraft/worldgen/density_function/overworld/**")

    // Likewise, do not override the core minecraft:overworld dimension type.
    // The custom dimension_type JSON in the minecraft namespace changes the
    // global min_y/height for the overworld, which has led to crashes during
    // chunk generation (ArrayIndexOutOfBoundsException in ChunkAccess
    // when the engine computes section indices).
    //
    // By excluding this file from the shipped jar, vanilla's own
    // minecraft:overworld dimension_type definition is used again. The
    // VerticalExpansion tall-world preset still works via its custom
    // noise settings without globally patching the overworld dimension.
    exclude("data/minecraft/dimension_type/overworld.json")
}