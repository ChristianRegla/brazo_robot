package com.example.robot.ui.navigation

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.robot.ui.screens.AboutScreen
import com.example.robot.ui.screens.MainScreen
import com.example.robot.ui.screens.SettingsScreen
import com.example.robot.ui.screens.WelcomeScreen
import com.example.robot.viewmodel.SettingsViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))

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
                onGoHome = {
                    navController.navigate(Destinations.WELCOME_ROUTE) {
                        popUpTo(Destinations.MAIN_ROUTE) { inclusive = true }
                    }
                },
                onGoToAbout = { navController.navigate(Destinations.ABOUT_ROUTE) },
                onGoToSettings = { navController.navigate(Destinations.SETTINGS_ROUTE) },
                settingsViewModel = settingsViewModel
            )
        }
        composable(Destinations.ABOUT_ROUTE) {
            AboutScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Destinations.SETTINGS_ROUTE) {
            SettingsScreen(settingsViewModel = settingsViewModel,
                onBackClick = { navController.popBackStack() },
                onGoToAbout = { navController.navigate(Destinations.ABOUT_ROUTE) }
            )
        }
    }
}

class SettingsViewModelFactory(private val context: Context) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}