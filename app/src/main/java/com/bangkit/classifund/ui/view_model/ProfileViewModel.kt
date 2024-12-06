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
                    val settings = FirebaseFirestoreSettings.Builder()
                        .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                        .setPersistenceEnabled(true)
                        .build()
                    db.firestoreSettings = settings

                    db.collection("Users")
                        .document(userId)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                Log.e("ProfileViewModel", "Listen failed", error)
                                setProfileFromAuth()
                                return@addSnapshotListener
                            }

                            if (snapshot != null && snapshot.exists()) {
                                val email = auth.currentUser?.email ?: ""
                                val name = snapshot.getString("name") ?: ""

                                Log.d("ProfileViewModel", "Raw data: ${snapshot.data}")

                                _user.value = UserProfile(email = email, name = name)
                            } else {
                                Log.d("ProfileViewModel", "No existing profile found, creating new profile")
                                setProfileFromAuth()
                            }
                        }
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error in loadUserProfile", e)
                setProfileFromAuth()
            }
        }
    }

    private fun setProfileFromAuth() {
        val email = auth.currentUser?.email ?: ""
        val name = auth.currentUser?.displayName ?: email.substringBefore("@")

        val userProfile = UserProfile(email = email, name = name)
        _user.value = userProfile

        auth.currentUser?.uid?.let { userId ->
            db.collection("Users")
                .document(userId)
                .set(userProfile)
        }
    }
    fun updateProfile(name: String) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                db.collection("Users")
                    .document(userId)
                    .update("name", name)
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