// ============================================================
// Project-level build.gradle.kts
// Rename this file to build.gradle.kts and place it at the ROOT of the project
// (same folder as settings.gradle.kts, NOT inside app/)
// ============================================================

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
