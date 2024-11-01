package com.yoyobeep.test.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import kotlinx.coroutines.delay



@Composable
fun SplashScreen(modifier: Modifier = Modifier, logo: Painter, backgroundColor: Color = Color.White, navController: NavController) {


    LaunchedEffect(key1 = true) {
        delay(2000) // Simulate splash screen duration (adjust as needed)
        navController.navigate("loginscreen")
    }


        Box(
            modifier = modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(color = backgroundColor)
                },
            contentAlignment = Alignment.Center // Align content in the center
        ) {
            Image(painter = logo,
                contentDescription = "App Logo",
                contentScale = ContentScale.Fit)
        }

}