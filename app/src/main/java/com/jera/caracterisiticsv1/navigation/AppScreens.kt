package com.jera.caracterisiticsv1.navigation

sealed class AppScreens(val route:String) {
    object SplashScreen: AppScreens("splash_screen")
    object MainScreen: AppScreens("main_screen")
    object CameraScreen: AppScreens("camera_screen")
    object SettingsScreen: AppScreens("settings_screen")
    object ResultsScreen: AppScreens("results_screen")
    object GalleryScreen: AppScreens("gallery_screen")
    object GarageScreen: AppScreens("garage_screen")
}