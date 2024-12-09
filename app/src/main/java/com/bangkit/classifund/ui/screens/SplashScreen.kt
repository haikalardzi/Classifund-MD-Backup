package com.bangkit.classifund.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect

@Composable
fun SplashScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1BBFA8)),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        // Menampilkan logo aplikasi
        Image(
            painter = painterResource(id = com.bangkit.classifund.R.drawable.logo1),
            contentDescription = "Logo Aplikasi"
        )
    }

    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("home") {
            popUpTo("login") { inclusive = true }
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    val navController = rememberNavController()
    SplashScreen(navController = navController)
}

