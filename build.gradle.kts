plugins {
    id("java")
}

group = "top.focess"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("top.focess:focess-util:1.1.17")
    implementation("top.focess:focess-socket:1.1.6")
    implementation("top.focess:focess-command:1.3.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "top.focess.mahjong.Launcher"
        )
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/**")
    }
}