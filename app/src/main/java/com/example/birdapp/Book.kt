package com.example.birdapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.WindowInsets
import androidx.navigation.NavController

@Composable
fun BookScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(2) } // Guide tab selected

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = WindowInsets.safeDrawing
                    .asPaddingValues()
                    .calculateTopPadding() + 16.dp,
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
            // 🔍 Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, shape = RoundedCornerShape(25.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        textStyle = TextStyle(fontSize = 16.sp),
                        decorationBox = { innerTextField ->
                            if (searchText.isEmpty()) {
                                Text(
                                    text = "Search...",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🎮 Gamified List Button
            Button(
                onClick = { navController.navigate("gamifiedlist") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Gamified List",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 📖 Title and Description
            Text(
                text = "BIRD ARCHIVES",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "See All the Birds You Have Discovered",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

        }

        // ⛵ Bottom Navigation Bar
        BottomNavigationBar(navController, selectedTab) { newIndex ->
            selectedTab = newIndex
            when (newIndex) {
                0 -> navController.navigate("homepage")
                1 -> navController.navigate("cameraalt")
                2 -> navController.navigate("book")
                3 -> navController.navigate("settings")
            }
        }
    }
}
