package com.example.robot.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.robot.R
import com.example.robot.ui.theme.RobotTheme

@Composable
fun MainScreen() {
    RobotTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            ConstraintLayout(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
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
                    onClick = { /* Aquí irá la acción principal */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .constrainAs(button) {
                            top.linkTo(materia.bottom)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .width(200.dp)
                ) {
                    Text(text = "Obtener datos del robot")
                }
            }
        }
    }
}