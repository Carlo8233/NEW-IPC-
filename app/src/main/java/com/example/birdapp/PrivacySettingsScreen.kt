package com.example.birdapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun PrivacySettingsScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val database = FirebaseDatabase.getInstance().reference
    val userId = user?.uid ?: ""

    var shareData by remember { mutableStateOf(true) }
    var allowLocation by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        database.child("settings").child(userId).child("privacy").get().addOnSuccessListener { snapshot ->
            shareData = snapshot.child("shareData").getValue(Boolean::class.java) ?: true
            allowLocation = snapshot.child("allowLocation").getValue(Boolean::class.java) ?: true
        }
    }

    fun savePrivacySettings() {
        database.child("settings").child(userId).child("privacy").setValue(
            mapOf(
                "shareData" to shareData,
                "allowLocation" to allowLocation
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Share My Birding Data")
            Switch(checked = shareData, onCheckedChange = {
                shareData = it
                savePrivacySettings()
            })
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Allow Location Access")
            Switch(checked = allowLocation, onCheckedChange = {
                allowLocation = it
                savePrivacySettings()
            })
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}
