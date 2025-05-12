package com.example.birdapp

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CameraAltScreen(navController: NavController) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Identify Bird Species with an Image or Sound",
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.cameraalt), // Replace with actual drawable
            contentDescription = "Bird Identification"
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ✅ Capture Image Button (Opens Camera)
        Button(
            onClick = {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (cameraIntent.resolveActivity(context.packageManager) != null) {
                    (context as Activity).startActivityForResult(cameraIntent, 100)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "CAPTURE IMAGE", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Record Audio Button (Navigates to Recording Screen)
        Button(
            onClick = { navController.navigate("record_audio") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "RECORD AUDIO", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        // ✅ Bottom Navigation Bar (Highlight CameraAlt as selected)
        BottomNavigationBar(navController, selectedTab = 1) { newIndex ->
            when (newIndex) {
                0 -> navController.navigate("homepage") // Home
                1 -> { /* Stay on Camera */ }
                2 -> navController.navigate("book") // Guide
                3 -> navController.navigate("settings") // Settings
            }
        }
    }
}
