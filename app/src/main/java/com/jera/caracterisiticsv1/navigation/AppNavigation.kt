package com.jera.caracterisiticsv1.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jera.caracterisiticsv1.MainScreen
import com.jera.caracterisiticsv1.SplashScreen
import com.jera.caracterisiticsv1.CameraScreen
import com.jera.caracterisiticsv1.SettingsScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreens.SplashScreen.route
    ){
        composable(AppScreens.SplashScreen.route) {
            SplashScreen(navController)
        }
        composable(AppScreens.MainScreen.route){
            MainScreen(navController)
        }
        composable(AppScreens.CameraScreen.route){
            CameraScreen(navController)
        }
        composable(AppScreens.SettingsScreen.route){
             SettingsScreen(navController)
        }
    }
}