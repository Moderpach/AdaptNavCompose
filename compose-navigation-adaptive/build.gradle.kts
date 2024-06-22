
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    `maven-publish`
}

android {
    namespace = "moderpach.compose.navigation.adaptive"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    publishing {
        multipleVariants {
            allVariants()
            withSourcesJar()
            withJavadocJar()
        }
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.navigation.compose)
    implementation(libs.navigation.testing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

//Publish

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "own.moderpach"
            artifactId = "compose-navigation-adaptive"
            version = "0.1"

            pom {
                name = "compose-navigation-adaptive"
                description = """
                    AdaptNavCompose provides a clean, extendable and elegant implementation of
                    adaptive layout and navigation for Jetpack Compose.
                """.trimIndent()
                url = "https://github.com/Moderpach/AdaptNavCompose"
                licenses {
                    licenses {
                        name = "Apache License 2.0"
                        url = "https://github.com/Moderpach/AdaptNavCompose/blob/master/LICENSE"
                    }
                }
                developers {
                    developer {
                        id = "moderpach"
                        name = "Moderpach"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/Moderpach/AdaptNavCompose.git"
                    developerConnection = "scm:git:ssh://github.com/Moderpach/AdaptNavCompose.git"
                    url = "https://github.com/Moderpach/AdaptNavCompose"
                }
            }

            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        maven {
            name = "compose-navigation-adaptive"
            url = uri("${project.buildDir}/repo")
        }
    }
}