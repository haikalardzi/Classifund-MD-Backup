// EditTransactionViewModel.kt
package com.bangkit.classifund.ui.transaction

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.classifund.model.Transaction
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

class EditTransactionViewModel : ViewModel() {
    private val db = Firebase.firestore("classifund")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val _transaction = MutableStateFlow<Transaction?>(null)
    val transaction: StateFlow<Transaction?> = _transaction

    // In EditTransactionViewModel.kt
    fun getTransaction(transactionId: String) {
        viewModelScope.launch {
            try {
                if (userId.isNullOrEmpty()) {
                    Log.e("EditTransactionViewModel", "No authenticated user found")
                    return@launch
                }

                val snapshot = db.collection("Users")
                    .document(userId)
                    .collection("transactions")
                    .document(transactionId)
                    .get()
                    .await()

                if (snapshot.exists()) {
                    val timestamp = snapshot.getTimestamp("date")
                    val dateStr = timestamp?.toDate()?.toString() ?: ""

                    val transaction = Transaction(
                        id = transactionId,
                        date = dateStr,
                        category = snapshot.getString("category") ?: "",
                        type = snapshot.getString("type") ?: "",
                        description = snapshot.getString("description") ?: "",
                        total = snapshot.getLong("total") ?: 0
                    )
                    _transaction.value = transaction
                } else {
                    Log.e("EditTransactionViewModel", "Transaction not found")
                    _transaction.value = null
                }
            } catch (e: Exception) {
                Log.e("EditTransactionViewModel", "Error fetching transaction", e)
                _transaction.value = null
            }
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                if (userId.isNullOrEmpty()) {
                    Log.e("EditTransactionViewModel", "No authenticated user found")
                    return@launch
                }

                // Parse the date string to Timestamp
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val date = dateFormat.parse(transaction.date)
                val timestamp = date?.let { Timestamp(it) }

                val transactionData = hashMapOf(
                    "category" to transaction.category,
                    "date" to timestamp,
                    "description" to transaction.description,
                    "total" to transaction.total,
                    "type" to transaction.type
                )

                db.collection("Users")
                    .document(userId)
                    .collection("transactions")
                    .document(transaction.id)
                    .update(transactionData as Map<String, Any>)
                    .addOnSuccessListener {
                        Log.d("EditTransactionViewModel", "Transaction updated successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("EditTransactionViewModel", "Error updating transaction", e)
                    }
            } catch (e: Exception) {
                Log.e("EditTransactionViewModel", "Unexpected error during Firestore operation", e)
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                if (userId.isNullOrEmpty()) {
                    Log.e("EditTransactionViewModel", "No authenticated user found")
                    return@launch
                }

                db.collection("Users")
                    .document(userId)
                    .collection("transactions")
                    .document(transaction.id)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("EditTransactionViewModel", "Transaction deleted successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("EditTransactionViewModel", "Error deleting transaction", e)
                    }
            } catch (e: Exception) {
                Log.e("EditTransactionViewModel", "Unexpected error during Firestore operation", e)
            }
        }
    }
}