package com.bangkit.classifund.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bangkit.classifund.ui.transaction.EditTransactionViewModel
import androidx.compose.runtime.collectAsState
@Composable
fun EditTransactionScreen(
    navController: NavController,
    transactionId: String,
    viewModel: EditTransactionViewModel = viewModel()
) {
    val transaction by viewModel.transaction.collectAsState()

    LaunchedEffect(transactionId) {
        viewModel.getTransaction(transactionId)
    }

    transaction?.let { tx ->
        var category by remember { mutableStateOf(tx.category) }
        var type by remember { mutableStateOf(tx.type) }
        var description by remember { mutableStateOf(tx.description) }
        var amount by remember { mutableStateOf(tx.total.toString()) }
        var date by remember { mutableStateOf(tx.date) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("Edit Transaction", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.width(40.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            DropdownMenuField(
                label = "Type",
                items = listOf("Income", "Expense"),
                selectedItem = type,
                onItemSelected = { type = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (type == "Expense") {
                DropdownMenuField(
                    label = "Category",
                    items = listOf("Shopping", "Food", "Transportation", "Health", "Other"),
                    selectedItem = category,
                    onItemSelected = { category = it }
                )
            } else {
                DropdownMenuField(
                    label = "Category",
                    items = listOf("Salary", "Investment", "Other"),
                    selectedItem = category,
                    onItemSelected = { category = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                label = "Description",
                value = description,
                onValueChange = { description = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                label = "Amount",
                value = amount,
                onValueChange = { amount = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DatePickerField(
                selectedDate = date,
                onDateSelected = { date = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.deleteTransaction(tx)
                        navController.navigateUp()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }

                Button(
                    onClick = {
                        val updatedTransaction = tx.copy(
                            category = category,
                            type = type,
                            description = description,
                            total = amount.toLongOrNull() ?: 0,
                            date = date
                        )
                        viewModel.updateTransaction(updatedTransaction)
                        navController.navigateUp()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Save")
                }
            }
        }
    } ?: Text("Loading...")
}