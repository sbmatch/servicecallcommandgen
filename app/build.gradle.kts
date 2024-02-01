plugins {
    id("com.android.application")
}

android {
    namespace = "com.ma.servicecallcommandgenerate"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ma.servicecallcommandgenerate"
        minSdk = 23
        targetSdk = 34
        versionCode = 111
        versionName = "1.1"

        //testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.preference:preference:1.2.1")
    implementation ("dev.rikka.rikkax.preference:simplemenu-preference:1.0.3")
    implementation ("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")
}