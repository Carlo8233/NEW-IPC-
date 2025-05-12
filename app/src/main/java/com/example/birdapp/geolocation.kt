package com.example.birdapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@Composable
fun GeolocationScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(1) } // Geolocation tab selected
    var locationText by remember { mutableStateOf("GEO LOCATION SCREEN.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding() + 16.dp,
                bottom = 0.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            // 🌍 Location Info
            Text(
                text = locationText,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🔧 You can add more geolocation components here
        }

        // ⛵ Bottom Navigation Bar
        GeolocationBottomNavigationBar(navController, selectedTab) { newIndex ->
            selectedTab = newIndex
            when (newIndex) {
                0 -> navController.navigate("homepage")
                1 -> navController.navigate("geolocation")
                2 -> navController.navigate("cameraalt")
                3 -> navController.navigate("book")
                4 -> navController.navigate("settings")
            }
        }
    }
}

@Composable
fun GeolocationBottomNavigationBar(
    navController: NavController,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        val navItems = listOf(
            Pair(Icons.Outlined.Home, "Home"),
            Pair(Icons.Outlined.LocationOn, "Geolocation"),
            Pair(Icons.Outlined.CameraAlt, "Capture"),
            Pair(Icons.Outlined.Book, "Guide"),
            Pair(Icons.Outlined.Settings, "Settings")
        )

        navItems.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(item.first, contentDescription = item.second, tint = Color(0xFF2E7D32))
                },
                label = { Text(item.second) },
                selected = selectedTab == index,
                onClick = {
                    onTabSelected(index)
                    when (index) {
                        0 -> navController.navigate("homepage")
                        1 -> navController.navigate("geolocation")
                        2 -> navController.navigate("cameraalt")
                        3 -> navController.navigate("book")
                        4 -> navController.navigate("settings")
                    }
                }
            )
        }
    }
}
