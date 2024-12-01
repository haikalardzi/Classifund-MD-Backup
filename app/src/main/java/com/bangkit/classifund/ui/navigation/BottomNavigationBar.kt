package com.bangkit.classifund.ui.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home") },
            icon = { Text("🏠") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("settings") },
            icon = { Text("⚙️") },
            label = { Text("Settings") }
        )
    }
}
