package com.bangkit.classifund.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun FaqPage(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Classifund FAQ",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        FaqItem(
            question = "What is Classifund?",
            answer = "Classifund is a personal finance app to manage income and expenses."
        )
        Spacer(modifier = Modifier.height(16.dp))
        FaqItem(
            question = "How do I sign up?",
            answer = "Visit the Sign Up page and enter your details."
        )
        Spacer(modifier = Modifier.height(16.dp))
        FaqItem(
            question = "How do I add an expense?",
            answer = "Go to Home, tap 'Add' under the nav bar, and enter the details."
        )
        Spacer(modifier = Modifier.height(16.dp))
        FaqItem(
            question = "How do I view my spending?",
            answer = "Your history is on the Analytics page. View by week or total."
        )
        Spacer(modifier = Modifier.height(16.dp))
        FaqItem(
            question = "Who can I contact for support?",
            answer = "Email support@classifund.com for any issues."
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun FaqItem(
    question: String,
    answer: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = question,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = answer,
            style = TextStyle(
                fontSize = 16.sp
            )
        )
    }
}