package com.bangkit.classifund.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bangkit.classifund.model.Transaction
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val selectedPeriod = remember { mutableStateOf("Today") }
    val periods = listOf("Today", "Week", "Month", "Year")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Summary Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryCard(
                type = "Income",
                amount = viewModel.getTotalAmount("income"),
                modifier = Modifier.weight(1f),
                backgroundColor = Color(0xFFE3F2FD)
            )
            SummaryCard(
                type = "Expense",
                amount = viewModel.getTotalAmount("expense"),
                modifier = Modifier.weight(1f),
                backgroundColor = Color(0xFFE0F2F1)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Period Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            periods.forEach { period ->
                PeriodTab(
                    text = period,
                    selected = selectedPeriod.value == period,
                    onClick = { selectedPeriod.value = period }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = selectedPeriod.value,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Transactions List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filteredTransactions = filterTransactionsByPeriod(transactions, selectedPeriod.value)
            items(filteredTransactions) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = { navController.navigate("edit_transaction/${transaction.id}") }
                )
            }
        }
    }
}

@Composable
fun SummaryCard(
    type: String,
    amount: Long,
    modifier: Modifier = Modifier,
    backgroundColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (type == "Income") Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                contentDescription = type,
                tint = if (type == "Income") Color(0xFF43A047) else Color(0xFFE53935)
            )
            Text(text = type)
            Text(
                text = "Rp ${amount.toLocaleString()}",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun PeriodTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(27, 191, 168) else Color.LightGray,
            contentColor = if (selected) Color.White else Color.Black
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Text(text)
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryIcon(transaction.category)
            Column {
                Text(
                    text = transaction.category.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
        Text(
            text = "Rp ${transaction.total.toLocaleString()}",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun CategoryIcon(category: String) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(27, 191, 168)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = when (category.lowercase()) {
                "shopping" -> Icons.Default.ShoppingCart
                "food" -> Icons.Default.Restaurant
                "transportation" -> Icons.Default.DirectionsCar
                "education" -> Icons.Default.School
                else -> Icons.Default.Category
            },
            contentDescription = category,
            tint = Color.White
        )
    }
}

fun Long.toLocaleString(): String {
    return String.format("%,d", this)
}

fun filterTransactionsByPeriod(transactions: List<Transaction>, period: String): List<Transaction> {
    val dateFormat = SimpleDateFormat("MMMM d, yyyy 'at' hh:mm:ss a z", Locale.US)
    val currentDate = Calendar.getInstance()

    return transactions.filter { transaction ->
        try {
            val transactionDate = Calendar.getInstance().apply {
                time = dateFormat.parse(transaction.date) ?: return@filter false
            }

            when (period) {
                "Today" -> isSameDay(transactionDate, currentDate)
                "Week" -> isSameWeek(transactionDate, currentDate)
                "Month" -> isSameMonth(transactionDate, currentDate)
                "Year" -> isSameYear(transactionDate, currentDate)
                else -> true
            }
        } catch (e: Exception) {
            false
        }
    }.sortedByDescending { dateFormat.parse(it.date) }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun isSameWeek(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)
}

private fun isSameMonth(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
}

private fun isSameYear(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
}