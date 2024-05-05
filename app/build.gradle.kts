plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    // Google Services Gradle Plugin (Firebase SDK)
    id("com.google.gms.google-services")

    // Kotlin Parcelize
    id("kotlin-parcelize")
}

android {
    namespace = "com.estevaosouza.whatsappproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.estevaosouza.whatsappproject"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    // ViewBinding from Jetpack
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))

    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics")

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx")

    // Firebase Cloud Firestore (DB)
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Firebase Cloud Storage
    implementation("com.google.firebase:firebase-storage-ktx")

    // Picasso
    implementation("com.squareup.picasso:picasso:2.8")

    // ViewModel
    implementation("androidx.activity:activity-ktx:1.9.0")

    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}