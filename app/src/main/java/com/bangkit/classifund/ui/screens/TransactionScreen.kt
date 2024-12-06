package com.bangkit.classifund.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bangkit.classifund.ui.transaction.AddTransactionViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun TransactionScreen(viewModel: AddTransactionViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val transactionType by viewModel.transactionType.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val category by viewModel.category.collectAsState()
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
        if (transactionType == "Expense"){
            // Category Dropdown
            DropdownMenuField(
                label = "Category",
                items = listOf("Shopping", "Food", "Transport"),
                selectedItem = category,
                onItemSelected = { viewModel.updateCategory(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Description Field
        InputField(
            label = "Description",
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


        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        SaveButton(onSaveClicked = { viewModel.saveTransaction() })
    }
}

@Composable
fun TransactionTypeSelector(selectedType: String, onTypeSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.LightGray)
            .height(48.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        // Expense Button
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    if (selectedType == "Expense") Color(27, 191, 168, ) else Color.LightGray
                )
                .clip(RoundedCornerShape(24.dp))
                .clickable { onTypeSelected("Expense") },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Expense",
                color = if (selectedType == "Expense") Color.White else Color(27, 191, 168, ),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Income Button
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    if (selectedType == "Income") Color(27, 191, 168, ) else Color.LightGray
                )
                .clip(RoundedCornerShape(24.dp))
                .clickable { onTypeSelected("Income") },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Income",
                color = if (selectedType == "Income") Color.White else Color(27, 191, 168, ),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



@Composable
fun DatePickerField(selectedDate: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val defaultDate = LocalDate.now().format(DateTimeFormatter.ofPattern("d/M/yyyy"))
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
        label = { Text("Select Date") },
        onValueChange = {},
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(Icons.Default.DateRange, contentDescription = "Select Date", tint = Color(27, 191, 168, ))
            }
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color(27, 191, 168, ),
            unfocusedTextColor = Color(27, 191, 168, )
        ),
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        textStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    )
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color(27, 191, 168, ),
            unfocusedTextColor = Color(27, 191, 168, )
        ),
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        textStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
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
    TextField(
        value = selectedItem ?: "",
        onValueChange = {}, // No manual editing allowed for dropdown field
        label = { Text(label) },
        modifier = Modifier
            .focusRequester(focusRequester)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { expanded = true },
        readOnly = true, // Prevent keyboard input
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color(27, 191, 168, ),
            unfocusedTextColor = Color(27, 191, 168, )
        ),
        textStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        ),

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
fun SaveButton(onSaveClicked: () -> Unit) {
    Button(
        onClick = onSaveClicked,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(27, 191, 168, ))
    ) {
        Text("Save")
    }
}
