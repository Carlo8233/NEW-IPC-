package com.example.birdapp

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.Alignment



@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to BirdApp!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { navController.navigate("signupScreen") },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Green,
                contentColor = Color.White
            )
        ) {
            Text("Go to Signup")
        }
    }
}

// ✅ Keep only the function (NO SignupScreen definition here!)
fun signUpUser(email: String, password: String, context: Context, auth: FirebaseAuth, navController: NavHostController) {
    if (email.isBlank() || password.isBlank()) {
        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_LONG).show()
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                    if (verificationTask.isSuccessful) {
                        Toast.makeText(context, "Verification email sent. Please check your email.", Toast.LENGTH_LONG).show()

                        // ✅ Navigate to confirmation screen after successful signup
                        navController.navigate("confirmationScreen")
                    } else {
                        Toast.makeText(context, "Failed to send verification email.", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(context, "Signup failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
}
