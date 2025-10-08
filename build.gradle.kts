// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.properties["buildDir"])
}

val compileSdk by extra(34)
val minSdk by extra(24)
val targetSdk by extra(34)
val ndkVersion by extra("29.0.14033849")

