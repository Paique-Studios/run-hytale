plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

gradlePlugin {
    plugins {
        create("hytaleServer") {
            id = "com.hytale.server"
            implementationClass = "HytaleServerPlugin"
        }
    }
}
