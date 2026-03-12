plugins {
    alias(libs.plugins.android.application)

    // add the google services gradle plugin
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.walrusevents"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.walrusevents"
        minSdk = 35
        targetSdk = 36
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
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Firestore dependency
    implementation("com.google.firebase:firebase-storage:20.3.0")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.10.0"))


    // Given by Firebase during initialization
    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")


    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries


    // Dependencies for ZXing (used for generating QR codes
    implementation("com.google.zxing:core:3.5.3") // Check for the latest version
    implementation("com.journeyapps:zxing-android-embedded:4.3.0") // Simplifies bitmap generation

    // Dependencies for Glide (used for converting a URI into a Bitmap
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // This 'annotationProcessor' line is what makes Glide smart
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
}