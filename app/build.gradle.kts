plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.senva"
    compileSdk = 35
    buildFeatures {
        compose = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "19"
    }

    defaultConfig {
        applicationId = "com.example.senva"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    // Declare the dependency for the Cloud Firestore library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-firestore")
    // Dependencia de Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    // annotationProcessor: permite generar automáticamente código interno que Glide necesita.
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    // Dependencia de credenciales con google:
    implementation("androidx.credentials:credentials:1.6.0-alpha03")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0-alpha03")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.3")

    implementation("com.google.firebase:firebase-auth-ktx")
    // Dependencia Fragments:
    implementation("androidx.fragment:fragment-ktx:1.8.8")
}