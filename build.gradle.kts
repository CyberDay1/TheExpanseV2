plugins {
    id("java")
}

version = project.property("mod_version")!!
group = project.property("maven_group")!!

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenLocal()
    maven("https://maven.neoforged.net/releases") {
        name = "NeoForged"
    }
    maven("https://maven.neoforged.net/releases/net/neoforged/") {
        name = "NeoForgedDev"
    }
    mavenCentral()
}

dependencies {
    implementation("net.neoforged:neoforge:21.1.209")
    
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