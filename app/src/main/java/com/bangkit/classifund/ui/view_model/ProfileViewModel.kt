package com.bangkit.classifund.ui.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore("classifund")
    private val _user = MutableStateFlow<UserProfile>(UserProfile())
    val user = _user.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val snapshot = db.collection("Users")
                        .document(userId)
                        .get()
                        .await()

                    if (snapshot.exists()) {
                        val email = auth.currentUser?.email ?: ""
                        val name = snapshot.getString("name") ?: ""
                        Log.d("ProfileViewModel", "Raw data: ${snapshot.data}")
                        _user.value = UserProfile(email = email, name = name)
                    } else {
                        Log.d("ProfileViewModel", "No existing profile found, creating new profile")
                        setProfileFromAuth()
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error in loadUserProfile", e)
                setProfileFromAuth()
            }
        }
    }

    private fun setProfileFromAuth() {
        viewModelScope.launch {
            try {
                val email = auth.currentUser?.email ?: ""
                val name = auth.currentUser?.displayName ?: email.substringBefore("@")
                val userProfile = UserProfile(email = email, name = name)

                auth.currentUser?.uid?.let { userId ->
                    db.collection("Users")
                        .document(userId)
                        .set(userProfile)
                        .await()

                    _user.value = userProfile
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error setting profile from auth", e)
            }
        }
    }

    fun updateProfile(name: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val updates = hashMapOf<String, Any>("name" to name)

                    db.collection("Users")
                        .document(userId)
                        .update(updates)
                        .await()

                    _user.value = _user.value.copy(name = name)
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating profile", e)
            }
        }
    }

    fun logout() {
        auth.signOut()

    }
}

data class UserProfile(
    val email: String = "",
    val name: String = ""
)