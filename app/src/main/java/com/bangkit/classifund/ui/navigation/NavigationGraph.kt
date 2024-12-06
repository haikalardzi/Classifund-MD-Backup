package com.bangkit.classifund.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bangkit.classifund.ui.screens.HomeScreen
import com.bangkit.classifund.ui.screens.SettingsScreen
import com.bangkit.classifund.ui.screens.TransactionScreen
import com.bangkit.classifund.ui.screens.ContactPage
import com.bangkit.classifund.ui.screens.EditTransactionScreen
import com.bangkit.classifund.ui.screens.FaqPage
import com.bangkit.classifund.ui.screens.ProfileScreen

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { FaqPage(navController) }
        composable("add_transaction") { TransactionScreen() }
        composable("analytics") { HomeScreen(navController) }
        composable("settings") { ProfileScreen(navController) }
        composable("help") { ContactPage(navController) }
        composable("faq") { FaqPage(navController) }
        composable("edit_transaction/{transactionId}") { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId")
            Log.d("NAVIGATION", "HERE")
            transactionId?.let { EditTransactionScreen(navController, it) }
        }
    }
}
