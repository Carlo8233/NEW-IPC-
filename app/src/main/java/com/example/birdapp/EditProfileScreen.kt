package com.example.birdapp

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EditProfileScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf(user?.displayName ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Input fields for name, email, and password
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("New Password") })

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            if (user != null) {
                loading = true
                // Update user profile info
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }

                user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (email != user.email) {
                            user.updateEmail(email).addOnCompleteListener { emailTask ->
                                if (emailTask.isSuccessful) {
                                    if (password.isNotEmpty()) {
                                        user.updatePassword(password).addOnCompleteListener { passwordTask ->
                                            if (passwordTask.isSuccessful) {
                                                // Save updated information to Firestore
                                                db.collection("users").document(user.uid)
                                                    .update("name", name, "email", email)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(navController.context, "Changes saved!", Toast.LENGTH_SHORT).show()
                                                        navController.popBackStack()
                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(navController.context, "Failed to save changes", Toast.LENGTH_SHORT).show()
                                                    }
                                            } else {
                                                Toast.makeText(navController.context, "Failed to update password", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(navController.context, "No password update", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(navController.context, "Failed to update email", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(navController.context, "No email update", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(navController.context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
                    loading = false
                }
            }
        }) {
            Text("Save Changes")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}
