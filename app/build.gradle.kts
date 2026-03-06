import java.util.Properties
import java.io.FileInputStream
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.inventarioapp"

    val local_properties = Properties()
    val archivo_local = project.rootProject.file("local.properties")

    if (archivo_local.exists()) {
        local_properties.load(FileInputStream(archivo_local))
    }

    signingConfigs{
        create("qaConfig") {
            storeFile = file(local_properties.getProperty("QA_STORE_FILE") ?: "")
            storePassword = local_properties.getProperty("QA_STORE_PASSWORD") ?: ""
            keyAlias = local_properties.getProperty("QA_KEY_ALIAS") ?: ""
            keyPassword = local_properties.getProperty("QA_KEY_PASSWORD") ?: ""
        }
    }

    compileSdk {
        version = release(36)
    }

    buildFeatures{
        buildConfig=true
    }

    defaultConfig {
        applicationId = "com.example.inventarioapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "env"

    productFlavors{
        create("dev") {
            dimension = "env"
            applicationId = "com.example.inventarioapp"
            versionNameSuffix = "-dev"

            val tokenDev = local_properties.getProperty("GOOGLE_WEB_CLIENT_ID") ?: ""
            buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$tokenDev\"")
        }

        create("qa") {
            dimension = "env"
            applicationId = "com.example.inventarioapp.qa"
            versionNameSuffix = "-qa"

            val tokenQa = local_properties.getProperty("GOOGLE_WEB_CLIENT_ID_QA") ?: ""
            buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$tokenQa\"")
        }

//        create("prod") {
//            dimension = "env"
//            applicationId = "com.recimalo.app"   // Play Store
//        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("qaConfig")
        }

        create("stagging") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("qaConfig")

            isDebuggable = true

            matchingFallbacks += listOf("releases")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.ui.text)
//    implementation(libs.firebase.auth.ktx)
//    implementation(libs.firebase.auth.common)
//    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")

    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.credentials:credentials:1.2.2")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

//    implementation ("com.google.firebase:firebase-auth-ktx:23.2.1")
//    implementation ("com.google.android.gms:play-services-auth:21.5.1")

}