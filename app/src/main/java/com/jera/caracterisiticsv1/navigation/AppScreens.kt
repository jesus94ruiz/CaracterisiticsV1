package com.jera.caracterisiticsv1.navigation

sealed class AppScreens(val route:String) {
    object SplashScreen: AppScreens("splash_screen")
    object MainScreen: AppScreens("main_screen")
    object CameraActivity: AppScreens("camera_activity")
    object CameraScreen: AppScreens("camera_screen")
    object SettingsScreen: AppScreens("settings_screen")
}