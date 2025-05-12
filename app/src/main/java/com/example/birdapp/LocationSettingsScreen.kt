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
fun LocationSettingsScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val database = FirebaseDatabase.getInstance().reference
    val userId = user?.uid ?: ""

    var enableLocation by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        database.child("settings").child(userId).child("locationTracking").get().addOnSuccessListener { snapshot ->
            enableLocation = snapshot.getValue(Boolean::class.java) ?: true
        }
    }

    fun saveLocationSetting() {
        database.child("settings").child(userId).child("locationTracking").setValue(enableLocation)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Enable Geolocation")
            Switch(checked = enableLocation, onCheckedChange = {
                enableLocation = it
                saveLocationSetting()
            })
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}
