package com.jera.caracterisiticsv1.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jera.caracterisiticsv1.*
import com.jera.caracterisiticsv1.screens.*


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
        composable(AppScreens.LoginScreen.route) {
            LoginScreen(navController)
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
        composable(AppScreens.ResultsScreen.route){
            ResultsScreen(navController, "")
        }
        composable(AppScreens.GalleryScreen.route){
            GalleryScreen(navController)
        }
        composable(AppScreens.GarageScreen.route){
            GarageScreen(navController)
        }
        composable(AppScreens.MapScreen.route){
            MapScreen(navController)
        }
        composable(AppScreens.ProfileScreen.route){
            ProfileScreen(navController)
        }
        composable(AppScreens.LeaderboardScreen.route){
            LeaderboardScreen(navController)
        }
        composable(AppScreens.ShowcaseScreen.route){
            ShowcaseScreen(navController)
        }
        composable(AppScreens.FriendsScreen.route){
            FriendsScreen(navController)
        }
        composable(
            route = AppScreens.CaptureRewardScreen.route,
            arguments = listOf(
                navArgument("xpGained") { type = NavType.IntType; defaultValue = 0 },
                navArgument("leveledUp") { type = NavType.BoolType; defaultValue = false },
                navArgument("newLevel") { type = NavType.IntType; defaultValue = 0 },
                navArgument("achievementsCount") { type = NavType.IntType; defaultValue = 0 }
            )
        ) { backStackEntry ->
            val xpGained = backStackEntry.arguments?.getInt("xpGained") ?: 0
            val leveledUp = backStackEntry.arguments?.getBoolean("leveledUp") ?: false
            val newLevel = backStackEntry.arguments?.getInt("newLevel") ?: 0
            val achievementsCount = backStackEntry.arguments?.getInt("achievementsCount") ?: 0
            CaptureRewardScreen(navController, xpGained, leveledUp, newLevel, achievementsCount)
        }
    }
}
