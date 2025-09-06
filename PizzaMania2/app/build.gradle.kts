plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // ✅ Firebase plugin
}

android {
    namespace = "com.kosala.pizza_mania"
    compileSdk = 36   // ✅ Updated (must be 35+ for androidx.activity:1.10.1)

    defaultConfig {
        applicationId = "com.kosala.pizza_mania"
        minSdk = 24
        targetSdk = 36   // ✅ Updated to match compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Core Android dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Google Maps + Location
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Firebase BOM (manages versions)
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth")

    // Extra UI components
    implementation("androidx.cardview:cardview:1.0.0")
}
