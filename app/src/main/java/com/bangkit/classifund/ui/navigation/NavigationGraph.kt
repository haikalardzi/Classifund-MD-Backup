package com.bangkit.classifund.ui.navigation

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bangkit.classifund.ui.login.LoginActivity
import com.bangkit.classifund.ui.screens.HomeScreen
import com.bangkit.classifund.ui.screens.SettingsScreen
import com.bangkit.classifund.ui.screens.TransactionScreen
import com.bangkit.classifund.ui.screens.ContactPage
import com.bangkit.classifund.ui.screens.DashboardScreen
import com.bangkit.classifund.ui.screens.EditTransactionScreen
import com.bangkit.classifund.ui.screens.FaqPage
import com.bangkit.classifund.ui.screens.LoginScreen
import com.bangkit.classifund.ui.screens.ProfileScreen

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { DashboardScreen(navController) }
        composable("add_transaction") { TransactionScreen() }
        composable("analytics") { HomeScreen(navController) }
        composable("settings") {
            ProfileScreen(
                navController = navController,
                onLogoutSuccess = {
                    val context = navController.context
                    val intent = Intent(context, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                }
            )
        }
        composable("help") { ContactPage(navController) }
        composable("faq") { FaqPage(navController) }
        composable("edit_transaction/{transactionId}") { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId")
            Log.d("NAVIGATION", "HERE")
            transactionId?.let { EditTransactionScreen(navController, it) }
        }
    }
}
