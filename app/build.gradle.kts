plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) // 추가됨

}

android {
    namespace = "com.foo.sifpr2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.foo.sifpr2"
        minSdk = 26
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
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.extjunit)
    androidTestImplementation(libs.espressocore)
    implementation(libs.roomruntime)
    annotationProcessor(libs.roomcompiler)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

}