import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.publisher)
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
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.navigation.compose)
    implementation(libs.navigation.testing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

//Publish
mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}

mavenPublishing {
    coordinates("io.github.moderpach", "compose-navigation-adaptive", "0.0.1")

    pom {
        name.set("AdaptNavCompose")
        description.set("""
            AdaptNavCompose provides a clean, extendable and elegant implementation of adaptive layout and navigation for Jetpack Compose.
        """.trimIndent())
        inceptionYear.set("2024")
        url.set("https://github.com/Moderpach/AdaptNavCompose")
        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://github.com/Moderpach/AdaptNavCompose/blob/master/LICENSE")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("moderpach")
                name.set("Moderpach")
                url.set("https://github.com/Moderpach")
            }
        }
        scm {
            url.set("https://github.com/Moderpach/AdaptNavCompose")
            connection.set("scm:git:https://github.com/Moderpach/AdaptNavCompose.git")
            developerConnection.set("scm:git:ssh://git@github.com:Moderpach/AdaptNavCompose.git")
        }
    }
}