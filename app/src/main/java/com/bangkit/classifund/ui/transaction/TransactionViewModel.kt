package com.bangkit.classifund.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddTransactionViewModel : ViewModel() {

    private val _transactionType = MutableStateFlow("Expense")
    val transactionType: StateFlow<String> = _transactionType

    private val _selectedDate = MutableStateFlow("")
    val selectedDate: StateFlow<String> = _selectedDate

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category

    private val _wallet = MutableStateFlow("")
    val wallet: StateFlow<String> = _wallet

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _total = MutableStateFlow("")
    val total: StateFlow<String> = _total

    fun updateTransactionType(type: String) {
        _transactionType.value = type
    }

    fun updateDate(date: String) {
        _selectedDate.value = date
    }

    fun updateCategory(category: String) {
        _category.value = category
    }

    fun updateWallet(wallet: String) {
        _wallet.value = wallet
    }

    fun updateDescription(desc: String) {
        _description.value = desc
    }

    fun updateTotal(amount: String) {
        _total.value = amount
    }

    fun saveTransaction() {
        viewModelScope.launch {
            // Save the transaction to the database or API
        }
    }
}