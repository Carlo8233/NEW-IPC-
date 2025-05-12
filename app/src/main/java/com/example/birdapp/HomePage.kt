package com.example.birdapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue

@Composable
fun HomePage(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    var birdPosts by remember { mutableStateOf<List<BirdPost>>(emptyList()) }

    // Fetching data from Firebase Realtime Database
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("birdPosts")

    // Set up a listener to get updates in real-time
    LaunchedEffect(Unit) {
        myRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val posts = mutableListOf<BirdPost>()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue<BirdPost>()
                    post?.let { posts.add(it) }
                }
                birdPosts = posts
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Toast.makeText(navController.context, "Failed to load posts", Toast.LENGTH_SHORT).show()
            }
        })
    }

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
                    .background(Color(0xFFF1F1F1), shape = RoundedCornerShape(25.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        textStyle = TextStyle(fontSize = 16.sp),
                        decorationBox = { innerTextField ->
                            if (searchText.isEmpty()) {
                                Text("Search...", fontSize = 16.sp, color = Color.Gray)
                            }
                            innerTextField()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 📌 Section Title
            Text(
                text = "BIRD HIGHLIGHTS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Discover Birds Spotted By Other Users",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🐦 Community Feed (Real-time Bird Posts)
            if (birdPosts.isNotEmpty()) {
                birdPosts.forEach { post ->
                    BirdCard(post)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                Text("No posts available", color = Color.Gray)
            }
        }

        // ✅ Bottom Navigation Bar
        BottomNavigationBar(navController, selectedTab) { newIndex ->
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
fun BirdCard(post: BirdPost) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        // Bird Image (Use imageUrl from post data)
        Image(
            painter = painterResource(id = R.drawable.whitethroatedsparrow), // Replace with actual image URL from Firebase if available
            contentDescription = "Bird Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Post Description
        Text(
            text = post.description,
            fontSize = 14.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // User and Time Info
        Text(
            text = "Posted by: ${post.userName} - ${post.timestamp}",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(containerColor = Color.White) {
        val navItems = listOf(
            Pair(Icons.Outlined.Home, "Home"),
            Pair(Icons.Outlined.LocationOn, "Map"),
            Pair(Icons.Outlined.CameraAlt, "Capture"),
            Pair(Icons.Outlined.Book, "Lifer List"),
            Pair(Icons.Outlined.Settings, "Settings")
        )

        navItems.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.first,
                        contentDescription = item.second,
                        tint = Color(0xFF2E7D32)
                    )
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

// Data model for BirdPost
data class BirdPost(
    val imageUrl: String = "",
    val description: String = "",
    val userName: String = "",
    val timestamp: Long = 0
)
