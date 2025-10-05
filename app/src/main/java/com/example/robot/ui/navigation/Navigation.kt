package com.example.robot.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.robot.ui.screens.ChartScreen
import com.example.robot.ui.screens.MainScreen
import com.example.robot.ui.screens.WelcomeScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    AnimatedNavHost(
        navController = navController,
        startDestination = Destinations.WELCOME_ROUTE,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }) + fadeIn(animationSpec = tween(500))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(animationSpec = tween(400))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it }) + fadeIn(animationSpec = tween(500))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }) + fadeOut(animationSpec = tween(400))
        }
    ) {
        composable(Destinations.WELCOME_ROUTE) {
            WelcomeScreen(
                onStartClick = {
                    navController.navigate(Destinations.MAIN_ROUTE) {
                        popUpTo(Destinations.WELCOME_ROUTE) { inclusive = true }
                    }
                }
            )
        }
        composable(Destinations.MAIN_ROUTE) {
            MainScreen(
                onGoHome = { navController.navigate(Destinations.WELCOME_ROUTE) },
                onExit = { navController.navigate(Destinations.WELCOME_ROUTE) },
            )
        }
        composable(Destinations.CHART_ROUTE) {
            ChartScreen(
                onGoHome = { navController.navigate(Destinations.WELCOME_ROUTE) },
                onGoMain = { navController.navigate(Destinations.MAIN_ROUTE) }
            )
        }
    }
}