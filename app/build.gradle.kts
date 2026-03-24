plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.accountingandmanagement"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.accountingandmanagement"
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
}

dependencies {

    // Retrofit for the network connection
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // GSON converter to turn the Netlify JSON into Java objects
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.mindrot:jbcrypt:0.4")
    // Optional: Log network requests to your console (very helpful for debugging)
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    implementation ("com.google.firebase:firebase-messaging:23.4.0")
    implementation ("com.google.android.flexbox:flexbox:3.0.0")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("com.google.mlkit:barcode-scanning:17.3.0")
    implementation ("androidx.camera:camera-camera2:1.3.0")
    implementation ("androidx.camera:camera-lifecycle:1.3.0")
    implementation ("androidx.camera:camera-view:1.3.0")
    implementation ("androidx.fragment:fragment:1.6.2")
    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.gridlayout:gridlayout:1.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}