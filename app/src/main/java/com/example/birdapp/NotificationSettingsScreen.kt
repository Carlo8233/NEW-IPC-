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
fun NotificationSettingsScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val database = FirebaseDatabase.getInstance().reference
    val userId = user?.uid ?: ""

    var birdAlerts by remember { mutableStateOf(true) }
    var newSpeciesUpdates by remember { mutableStateOf(true) }
    var appNews by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        database.child("settings").child(userId).child("notifications").get().addOnSuccessListener { snapshot ->
            birdAlerts = snapshot.child("birdAlerts").getValue(Boolean::class.java) ?: true
            newSpeciesUpdates = snapshot.child("newSpeciesUpdates").getValue(Boolean::class.java) ?: true
            appNews = snapshot.child("appNews").getValue(Boolean::class.java) ?: true
        }
    }

    fun saveSettings() {
        database.child("settings").child(userId).child("notifications").setValue(
            mapOf(
                "birdAlerts" to birdAlerts,
                "newSpeciesUpdates" to newSpeciesUpdates,
                "appNews" to appNews
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
            Text("Bird Alerts")
            Switch(checked = birdAlerts, onCheckedChange = {
                birdAlerts = it
                saveSettings()
            })
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("New Species Updates")
            Switch(checked = newSpeciesUpdates, onCheckedChange = {
                newSpeciesUpdates = it
                saveSettings()
            })
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("App News")
            Switch(checked = appNews, onCheckedChange = {
                appNews = it
                saveSettings()
            })
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}
