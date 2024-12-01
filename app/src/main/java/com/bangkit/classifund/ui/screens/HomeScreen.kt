package com.bangkit.classifund.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme

@Composable
fun HomeScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Welcome to Home Screen!",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxSize()
        )
    }
}
