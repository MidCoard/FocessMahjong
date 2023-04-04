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
    implementation("top.focess:focess-util")
}

tasks.test {
    useJUnitPlatform()
}