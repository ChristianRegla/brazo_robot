package com.example.robot.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.robot.ui.screens.MainScreen
import com.example.robot.ui.screens.WelcomeScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.WELCOME_ROUTE,
        modifier = modifier
    ) {
        composable(Destinations.WELCOME_ROUTE) {
            WelcomeScreen(
                onNavigateToMain = {
                    navController.navigate(Destinations.MAIN_ROUTE) {
                        popUpTo(Destinations.WELCOME_ROUTE) { inclusive = true }
                    }
                }
            )
        }
        composable(Destinations.MAIN_ROUTE) {
            MainScreen()
        }
    }
}