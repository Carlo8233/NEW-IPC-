package com.example.birdapp

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import androidx.compose.runtime.saveable.rememberSaveable

// Data class to hold welcome page content
data class WelcomePageContent(
    val imageRes: Int,
    val title: String,
    val description: String
)

@Composable
fun WelcomePage(navController: NavHostController, context: Context) {
    val dataStoreManager = remember { DataStoreManager(context) }
    val coroutineScope = rememberCoroutineScope()
    var pageIndex by rememberSaveable { mutableStateOf(0) }  // Ensures state persistence

    val pages = listOf(
        WelcomePageContent(R.drawable.loginpic, "Discover Birds Easily", "Identify birds using AI-powered image and sound recognition."),
        WelcomePageContent(R.drawable.loginpic, "Enhance Your Birding Experience", "Get detailed insights and facts about different bird species."),
        WelcomePageContent(R.drawable.loginpic, "Join the Community", "Connect with fellow bird enthusiasts and contribute to conservation efforts.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = pages[pageIndex].imageRes),
            contentDescription = null,
            modifier = Modifier
                .width(300.dp)
                .height(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = pages[pageIndex].title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = pages[pageIndex].description, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { if (pageIndex > 0) pageIndex-- },
                modifier = Modifier.size(50.dp),
                enabled = pageIndex > 0
            ) {
                Icon(painter = painterResource(id = R.drawable.leftarrow), contentDescription = "Previous", tint = if (pageIndex > 0) Color.Black else Color.Gray)
            }

            IconButton(
                onClick = {
                    if (pageIndex < pages.size - 1) {
                        pageIndex++
                    } else {
                        coroutineScope.launch {
                            dataStoreManager.setHasSeenWelcome(true) // Save flag
                        }
                        navController.navigate("loginsignup")
                    }
                },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.rightarrow),
                    contentDescription = if (pageIndex < pages.size - 1) "Next" else "Go to Login/Signup",
                    tint = if (pageIndex < pages.size - 1) Color.Black else Color.Gray
                )
            }
        }
    }
}
