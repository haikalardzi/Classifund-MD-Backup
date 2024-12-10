package com.bangkit.classifund.ui.transaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.classifund.model.ApiResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Locale

class AddTransactionViewModel : ViewModel() {

    private val _transactionType = MutableStateFlow("Expense")
    val transactionType: StateFlow<String> = _transactionType

    private val _selectedDate = MutableStateFlow("")
    val selectedDate: StateFlow<String> = _selectedDate

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category

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

    fun updateDescription(desc: String) {
        _description.value = desc
    }

    fun updateTotal(amount: String) {
        _total.value = amount
    }

    private suspend fun requestClassification(desc: String): String {
        val client = HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000 // 30 seconds
            }
        }
        try {
            val response: HttpResponse  = client.post("https://mlmodel-560363491997.asia-southeast2.run.app/predict"){
                contentType(ContentType.Application.Json)
                setBody("""{"text": "$desc"}""")
            }
            val stringBody: String = response.body()
            return stringBody
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
            return ""
        } finally {
            client.close()
        }
    }

    fun saveTransaction() {

        viewModelScope.launch {
            val db = Firebase.firestore("classifund")
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId.isNullOrEmpty()) {
                Log.e("PRINT", "No authenticated user found")
                return@launch
            }
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val parsedDate = dateFormat.parse(_selectedDate.value) // Converts to java.util.Date

            if (_transactionType.value == "Expense") {
                val result = requestClassification(_description.value)


                // Build the transaction data
                val transactionData = hashMapOf(
                    "type" to _transactionType.value,
                    "date" to Timestamp(parsedDate!!),
                    "category" to Json.decodeFromString<ApiResponse>(result).predicted_category,
                    "description" to _description.value,
                    "total" to _total.value.toDoubleOrNull() // Fallback to 0.0 if invalid
                )

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
            } else {
                val transactionData = hashMapOf(
                    "type" to _transactionType.value,
                    "category" to "Income",
                    "date" to Timestamp(parsedDate!!),
                    "description" to _description.value,
                    "total" to _total.value.toDoubleOrNull() // Fallback to 0.0 if invalid
                )
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
                    Log.e("PRINT", "Error adding transaction", e)

                }
            }
        }
    }
}