plugins {
    id("com.android.application")
}

android {
    namespace = "cz.test.test1"
    compileSdk = 34

    defaultConfig {
        applicationId = "cz.CommunicatorApp"
        minSdk = 31
        targetSdk = 33
        versionCode = 1
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        resources {
            excludes += setOf("META-INF/NOTICE.md", "META-INF/LICENSE.md")
        }
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("io.coil-kt:coil-compose:2.1.0")
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation ("com.github.esafirm.android-image-picker:imagepicker:2.4.5")
    implementation ("com.opencsv:opencsv:5.6")
    implementation ("com.squareup.picasso:picasso:2.71828")


    implementation ("com.sun.mail:android-mail:1.6.6")
    implementation ("com.sun.mail:android-activation:1.6.7")

    implementation ("org.opencv:opencv:4.9.0")
    implementation ("com.github.yalantis:ucrop:2.2.6")

    implementation ("com.soundcloud.android:android-crop:1.0.1@aar")






}