package com.bangkit.classifund.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bangkit.classifund.model.Transaction
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.FloatEntry
data class ExpenseItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val amount: Long,
    val date: String
)

data class WeeklyData(
    val day: String,
    val expense: Int,
    val income: Int
)

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    var showBarChart by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Chart Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { showBarChart = true }) {
                Icon(
                    Icons.Default.BarChart,
                    contentDescription = "Bar Chart",
                    tint = if (showBarChart) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = { showBarChart = false }) {
                Icon(
                    Icons.Default.PieChart,
                    contentDescription = "Pie Chart",
                    tint = if (!showBarChart) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (showBarChart) {
            WeeklyChart(viewModel.getWeeklyData())
        } else {
            CircularSummary(viewModel)
        }

        // Type Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Button(
                onClick = { viewModel.onTypeSelected("expense") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(selectedType == "expense")
                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Expense")
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { viewModel.onTypeSelected("income") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(selectedType == "income")
                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Income")
            }
        }

        // Transaction List
        LazyColumn {
            items(transactions.filter { it.type.lowercase() == selectedType }) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onTransactionClick = { navController.navigate("edit_transaction/${transaction.id}") }
                )
                Divider()
            }
        }
    }
}

@Composable
fun WeeklyChart(weeklyData: Map<String, Long>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(vertical = 16.dp)
    ) {
        Chart(
            chart = columnChart(),
            model = entryModelOf(weeklyData.values.mapIndexed { index, value ->
                FloatEntry(index.toFloat(), value.toFloat())
            }),
            startAxis = startAxis(),
            bottomAxis = bottomAxis(
                valueFormatter = { value, _ ->
                    weeklyData.keys.elementAtOrNull(value.toInt()) ?: ""
                }
            )
        )
    }
}

@Composable
fun CircularSummary(viewModel: HomeViewModel) {
    val income = viewModel.getTotalAmount("income")
    val expense = viewModel.getTotalAmount("expense")
    val total = income - expense

    val progress = if (income > 0) {
        (income - expense).toFloat() / income.toFloat()
    } else 0f

    val normalizedProgress = progress.coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = normalizedProgress,
            modifier = Modifier.size(200.dp),
            color = if (total >= 0) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 12.dp
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Rp $total",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (total >= 0) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
            Text(
                if (total >= 0) "Surplus" else "Deficit",
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onTransactionClick: (Transaction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTransactionClick(transaction) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            val icon = when(transaction.category.lowercase()) {
                "food" -> Icons.Default.Restaurant
                "health" -> Icons.Default.HealthAndSafety
                "home" -> Icons.Default.Home
                "transportation" -> Icons.Default.DirectionsCar
                "other" -> Icons.Default.Payments
                else -> Icons.Default.Receipt
            }
            Icon(
                imageVector = icon,
                contentDescription = transaction.category,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                transaction.category,
                fontWeight = FontWeight.Medium
            )
            Text(
                transaction.description,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
        }

        Text(
            "${transaction.total}",
            fontWeight = FontWeight.Medium
        )
    }
}