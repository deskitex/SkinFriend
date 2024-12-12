plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.skinfriend"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.skinfriend"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            "String",
            "BASE_URL",
            "\"https://capstone-skinfriend.et.r.appspot.com/api/\"",
        )
        buildConfigField(
            "String",
            "WEB_CLIENT_ID",
            "\"119416210380-b197q9htkd41u8rq50pp9k4dufkb05ui.apps.googleusercontent.com\"",

            )

        buildConfigField("String", "API_KEY", "\"${project.findProperty("API_KEY")}\"")
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Fragmen dan Activity:
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.fragment:fragment-ktx:1.8.5")

//viewModelScope:
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

//LifecycleScope:
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

//Navigation:
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.4")

//LiveData:
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

//Retrofit:
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

//Room:
    implementation("androidx.room:room-runtime:2.5.0")
    ksp("androidx.room:room-compiler:2.5.0")
    implementation("androidx.room:room-ktx:2.5.0")

//Glide:
    implementation("com.github.bumptech.glide:glide:4.16.0")

//Datastore:
    implementation("androidx.datastore:datastore-preferences:1.1.1")

//Coroutines:
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

//WorkManager:
    implementation("androidx.work:work-runtime:2.10.0")

//Tensorflow:
    implementation("org.tensorflow:tensorflow-lite-metadata:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-task-vision-play-services:0.4.2")

//Tensorflow Google Play Service dan GPU:
    implementation("com.google.android.gms:play-services-tflite-support:16.1.0")
    implementation("com.google.android.gms:play-services-tflite-gpu:16.2.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.9.0")

//CameraX:
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
    
//Material Design :
    implementation("com.google.android.material:material:1.9.0")

    // Firebase Authentication
    implementation(libs.firebase.auth)

    // Credential Manager
    implementation ("androidx.credentials:credentials:1.3.0")
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.play.services.auth)
    implementation(libs.google.firebase.auth)

    //ImageCircular
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    //UCrop
    implementation("com.github.yalantis:ucrop:2.2.9-native")

}