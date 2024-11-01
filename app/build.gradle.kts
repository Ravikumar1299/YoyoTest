plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("org.jetbrains.kotlin.kapt")
//    id("com.google.devtools.ksp")
}

android {
    namespace = "com.yoyobeep.test"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.yoyobeep.test"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.play.services.base)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Navigation Compose dependency
    implementation(libs.androidx.navigation.compose)

    implementation ("androidx.compose.material:material:1.6.8")

    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.8")
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.29.1-alpha")

    implementation ("io.coil-kt:coil-compose:2.0.0")
//    implementation ("com.google.firebase:firebase-auth-ktx:21.1.0")
    implementation ("com.google.android.gms:play-services-auth:20.4.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.0")

    implementation ("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")


//    implementation("androidx.room:room-runtime:$roomVersion")
//    ksp("androidx.room:room-compiler:$roomVersion")
//    implementation("androidx.room:room-ktx:$roomVersion")

//    implementation ("com.google.accompanist:accompanist-permissions:0.26.2-beta")
//    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
//    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.0")
//
//    implementation ("com.google.api-client:google-api-client:1.33.0")
////    implementation ("com.google.api-client:google-api-client-extensions-android2:1.33.0")
////    implementation ("com.google.api-client:google-oauth-client:1.33.0")
//    implementation ("com.google.api-client:google-api-client-jackson2:1.33.0")
////    implementation ("com.google.accompanist:accompanist-glide:0.26.2-beta")

}