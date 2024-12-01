package com.bangkit.classifund.ui.login
import com.bangkit.classifund.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.bangkit.classifund.MainActivity
import com.bangkit.classifund.ui.screens.LoginScreen
import com.bangkit.classifund.ui.screens.SignUpScreen
import com.bangkit.classifund.ui.theme.ClassifundTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : ComponentActivity() {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            ClassifundTheme {
                var isLoginScreen by remember { mutableStateOf(true) }

                if (isLoginScreen) {
                    LoginScreen(
                        onNavigateToSignUp = { isLoginScreen = false },
                        onLogin = { email, password -> loginWithEmail(email, password) },
                        onGoogleSignIn = { googleSignIn() }
                    )
                } else {
                    SignUpScreen(
                        onNavigateToLogin = { isLoginScreen = true },
                        onSignUp = { name, email, password -> signUp(name, email, password) },
                        onGoogleSignIn = { googleSignIn() }
                    )
                }
            }
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and Password must not be empty!", Toast.LENGTH_SHORT).show()
            return
        }
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signUp(name: String, email: String, password: String) {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Email, Password, and Name must not be empty!", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long!", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid
                    Log.d("SignUp", "User registration successful. User ID: $userId")


                    Toast.makeText(this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()

//                    if (userId != null) {
//                        val db = FirebaseFirestore.getInstance()
//
//                        val userData = hashMapOf(
//                            "name" to name,
//                            "email" to email
//                        )
//
//                        db.collection("users").document(userId).set(userData)
//                            .addOnSuccessListener {
//                                Log.d("SignUp", "User data saved successfully for User ID: $userId")
//
//                                db.collection("users").document(userId)
//                                    .collection("transaction").document("default")
//                                    .set(emptyMap<String, Any>())
//                                    .addOnSuccessListener {
//                                        Log.d("SignUp", "Default transaction collection created successfully for User ID: $userId")
//                                        Toast.makeText(this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show()
//                                        startActivity(Intent(this, MainActivity::class.java))
//                                        finish()
//                                    }
//                                    .addOnFailureListener { e ->
//                                        Log.d("SignUp", "Failed to create default transaction collection: ${e.message}")
//                                        Toast.makeText(this, "Failed to create default transaction: ${e.message}", Toast.LENGTH_SHORT).show()
//                                    }
//                            }
//                            .addOnFailureListener { e ->
//                                Log.d("SignUp", "Failed to save user data: ${e.message}")
//                                Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
//                            }
//                    } else {
//                        Log.d("SignUp", "User ID is null after successful registration.")
//                        Toast.makeText(this, "Failed to retrieve user ID.", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    val errorMessage = task.exception?.message ?: "Unknown error"
//                    Log.d("SignUp", "User registration failed: $errorMessage")
//                    Toast.makeText(this, "Sign-Up Failed: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.result
                if (account != null) {
                    firebaseAuthWithGoogle(account.idToken!!)
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Google Sign-In Failed: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                Log.d("LoginActivity", "Google Sign-In Failed: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Google Sign-In Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
