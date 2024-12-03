@file:OptIn(ExperimentalMaterial3Api::class)

package com.bangkit.classifund.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bangkit.classifund.ui.transaction.AddTransactionViewModel
import java.util.Calendar

@Composable
fun TransactionScreen(viewModel: AddTransactionViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val transactionType by viewModel.transactionType.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val category by viewModel.category.collectAsState()
    val wallet by viewModel.wallet.collectAsState()
    val description by viewModel.description.collectAsState()
    val total by viewModel.total.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Transaction Type Selector
        TransactionTypeSelector(
            selectedType = transactionType,
            onTypeSelected = { viewModel.updateTransactionType(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date Picker
        DatePickerField(
            selectedDate = selectedDate,
            onDateSelected = { viewModel.updateDate(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Dropdown
        DropdownMenuField(
            label = "Category",
            items = listOf("Shopping", "Food", "Transport"),
            selectedItem = category,
            onItemSelected = { viewModel.updateCategory(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Wallet Dropdown
        DropdownMenuField(
            label = "Wallet",
            items = listOf("Cash", "Bank", "Credit Card"),
            selectedItem = wallet,
            onItemSelected = { viewModel.updateWallet(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description Field
        InputField(
            label = "Description (Optional)",
            value = description,
            onValueChange = { viewModel.updateDescription(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Total Field
        InputField(
            label = "Total",
            value = total,
            onValueChange = { viewModel.updateTotal(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Attachment Field
        AttachmentField(onAttachClicked = { /* Handle attachment */ })

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        SaveButton(onSaveClicked = { viewModel.saveTransaction() })
    }
}

@Composable
fun TransactionTypeSelector(selectedType: String, onTypeSelected: (String) -> Unit) {
    Row {
        Button(
            onClick = { onTypeSelected("Expense") },
            enabled = selectedType != "Expense",
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedType == "Expense") Color(0xFF00C853) else Color.LightGray
            )
        ) {
            Text("Expense")
        }
        Button(
            onClick = { onTypeSelected("Income") },
            enabled = selectedType != "Income",
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedType == "Income") Color(0xFF00C853) else Color.LightGray
            )
        ) {
            Text("Income")
        }
    }
}

@Composable
fun DatePickerField(selectedDate: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected("$dayOfMonth/${month + 1}/$year")
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    TextField(
        value = selectedDate,
        onValueChange = {},
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DropdownMenuField(
    label: String,
    items: List<String>,
    selectedItem: String?,
    onItemSelected: (String) -> Unit
) {
    // Track expanded state of the dropdown menu
    var expanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    // Outlined Text Field for dropdown
    OutlinedTextField(
        value = selectedItem ?: "",
        onValueChange = {}, // No manual editing allowed for dropdown field
        label = { Text(label) },
        modifier = Modifier
            .focusRequester(focusRequester)
            .clickable { expanded = true },
        readOnly = true, // Prevent keyboard input
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown Arrow",
                modifier = Modifier.clickable { expanded = true }
            )
        }
    )

    // Dropdown Menu
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                text = { Text(item) },
                onClick = {
                    onItemSelected(item) // Update the selected item
                    expanded = false // Close dropdown
                }
            )
        }
    }
}

@Composable
fun AttachmentField(onAttachClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.AddCircle, contentDescription = "Attach File")
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Add attachment",
            modifier = Modifier
                .clickable { onAttachClicked() }
                .padding(8.dp)
        )
    }
}
@Composable
fun SaveButton(onSaveClicked: () -> Unit) {
    Button(
        onClick = onSaveClicked,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
    ) {
        Text("Save")
    }
}
