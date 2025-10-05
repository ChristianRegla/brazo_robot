package com.example.robot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.robot.ui.screens.WelcomeScreen
import com.example.robot.ui.theme.RobotTheme

object Destinations {
    const val WELCOME_ROUTE = "welcome"
    const val MAIN_ROUTE = "main"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RobotTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.WELCOME_ROUTE,
        modifier = Modifier.fillMaxSize()
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
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Greeting(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()){
        val (backgroundBox, text, materia, button) = createRefs()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(backgroundBox) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.background_start),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            text = "Bienvenido al sistema de control del robot",
            modifier = Modifier.constrainAs(text) {
                top.linkTo(parent.top)
                bottom.linkTo(button.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Text(
            text = "Materia: Internet de las cosas",
            modifier = Modifier.constrainAs(materia) {
                top.linkTo(text.bottom)
                bottom.linkTo(button.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .constrainAs(button) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .width(200.dp)
        ) {
            Text(text = "Iniciar")
        }
    }

}