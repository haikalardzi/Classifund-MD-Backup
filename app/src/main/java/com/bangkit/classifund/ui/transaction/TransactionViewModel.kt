package com.bangkit.classifund.ui.transaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase

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
            val db = Firebase.firestore("classifund")
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId.isNullOrEmpty()) {
                Log.e("PRINT", "No authenticated user found")
                return@launch
            }

            // Build the transaction data
            val transactionData = hashMapOf(
                "transactionType" to _transactionType.value,
                "date" to _selectedDate.value,
                "category" to _category.value,
                "wallet" to _wallet.value,
                "description" to _description.value,
                "total" to _total.value.toDoubleOrNull() // Fallback to 0.0 if invalid
            )
            Log.d("PRINT", _transactionType.value)
            Log.d("PRINT", _selectedDate.value)
            Log.d("PRINT", _category.value)
            Log.d("PRINT", _wallet.value)
            Log.d("PRINT", _description.value)

            try {
                // Save the transaction under the user's collection
                db.collection("Users")
                    .document(userId)
                    .collection("transactions")
                    .add(transactionData)
                    .addOnSuccessListener { documentReference ->
                        Log.d("PRINT", "Transaction added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("PRINT", "Error adding transaction", e)
                    }
            } catch (e: Exception) {
                Log.e("PRINT", "Unexpected error during Firestore operation", e)
            }
        }
    }
}