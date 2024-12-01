package com.bangkit.classifund

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.bangkit.classifund.tempActivity.LoginActivity
import com.bangkit.classifund.ui.components.MainApp
import com.bangkit.classifund.ui.theme.ClassifundTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContent {
            ClassifundTheme {
                MainApp()
            }
        }
    }
}
