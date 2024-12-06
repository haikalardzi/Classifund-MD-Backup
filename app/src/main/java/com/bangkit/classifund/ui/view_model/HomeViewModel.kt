package com.bangkit.classifund.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.classifund.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class HomeViewModel : ViewModel() {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions = _transactions.asStateFlow()

    private val _selectedType = MutableStateFlow("expense")
    val selectedType = _selectedType.asStateFlow()

    init {
        loadTransactions()
    }

    fun onTypeSelected(type: String) {
        _selectedType.value = type
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId.isNullOrEmpty()) {
                Log.e("HomeViewModel", "No authenticated user found")
                return@launch
            }

            val db = Firebase.firestore("classifund")
            db.collection("Users")
                .document(userId)
                .collection("transactions")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("HomeViewModel", "Listen failed", e)
                        return@addSnapshotListener
                    }

                    val transactionsList = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            val dateTimestamp = doc.getTimestamp("date")
                            val dateStr = dateTimestamp?.toDate()?.let { SimpleDateFormat("MMMM d, yyyy 'at' hh:mm:ss a z", Locale.US).format(it) } ?: ""

                            Transaction(
                                id = doc.id,
                                category = doc.getString("category") ?: "",
                                date = dateStr,
                                description = doc.getString("description") ?: "",
                                total = doc.getLong("total") ?: 0L,
                                type = doc.getString("type")?.lowercase() ?: ""
                            )
                        } catch (e: Exception) {
                            Log.e("HomeViewModel", "Error parsing document", e)
                            null
                        }
                    } ?: emptyList()

                    _transactions.value = transactionsList
                }
        }
    }

    fun getTotalAmount(type: String): Long {
        return transactions.value
            .filter { it.type.lowercase() == type.lowercase() }
            .sumOf { it.total }
    }

    fun getWeeklyData(): Map<String, Long> {
        return transactions.value.groupBy { transaction ->
            try {
                val dateFormat = SimpleDateFormat("MMMM d, yyyy 'at' hh:mm:ss a z", Locale.US)
                val date = dateFormat.parse(transaction.date)
                SimpleDateFormat("EEE", Locale.US).format(date)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error parsing date: ${transaction.date}", e)
                "Unknown"
            }
        }.mapValues {
            it.value.sumOf { transaction -> transaction.total }
        }
    }
}