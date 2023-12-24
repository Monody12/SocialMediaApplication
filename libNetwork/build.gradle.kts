plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.libnetwork"
    compileSdk = 33

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    // 只参与编译，不参与打包
    compileOnly(project(mapOf("path" to ":libCommon")))
//    implementation(project(mapOf("path" to ":app")))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // okhttp
    implementation("com.squareup.okhttp3:okhttp:4.2.0")
    // okhttp的日志拦截器
    implementation("com.squareup.okhttp3:logging-interceptor:3.5.0")
//    // room
//    implementation("android.arch.persistence.room:runtime:1.1.1")
//    // room注解处理器
//    kapt("android.arch.persistence.room:compiler:1.1.1")
    // lifecycle
    implementation("android.arch.lifecycle:extensions:1.1.1")
    // room
    var room_version = "2.4.0"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    // lifecycle注解处理器
    kapt("android.arch.lifecycle:compiler:1.1.1")
    // annotation
    implementation("androidx.annotation:annotation:1.2.0")
    // fastjson
    implementation("com.alibaba:fastjson:1.2.62")
}