package com.bangkit.classifund.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bangkit.classifund.ui.screens.HomeScreen
import com.bangkit.classifund.ui.screens.SettingsScreen

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen() }
        composable("settings") { SettingsScreen(navController) }
    }
}
