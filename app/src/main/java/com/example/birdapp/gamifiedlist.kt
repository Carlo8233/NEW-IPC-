package com.example.birdapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Data model for a gamified bird item
data class GamifiedBird(
    val name: String,
    val imageRes: Int,
    val points: Int
)

// Sample bird data
val sampleBirds = listOf(
    GamifiedBird("White-Throated Sparrow", R.drawable.whitethroatedsparrow, 20),
    GamifiedBird("Scale-Feathered Malkoha", R.drawable.scalefeatheredmalkoha, 35),
    GamifiedBird("Palawan Peacock Pheasant", R.drawable.palawanpeacockpheasant, 50)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamifiedListScreen(navController: NavController, gamifiedListData: List<String>) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredBirds = sampleBirds.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // General padding around the entire screen
    ) {
        // 🔙 Back Button (Positioned at the top with padding and spacing)
        Button(
            onClick = { navController.navigate("book") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(top = 32.dp, bottom = 16.dp) // Increased space from the top
        ) {
            Text("← Back to Guidebook", fontSize = 16.sp)
        }

        // Search Bar (Positioned below the back button)
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Birds") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFF1F1F1),
                focusedIndicatorColor = Color(0xFF388E3C),
                unfocusedIndicatorColor = Color(0xFF388E3C)
            )
        )

        // Title and Description (Center-aligned)
        Text(
            text = "🎮 Your Bird Achievements",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32),
            modifier = Modifier.align(Alignment.CenterHorizontally) // Centered text
        )

        Spacer(modifier = Modifier.height(8.dp)) // Spacing between title and description

        Text(
            text = "Unlock badges and earn points by identifying birds!",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally) // Centered text
        )

        Spacer(modifier = Modifier.height(16.dp)) // Spacing before bird list

        // Bird List (Lazy Column)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Give more space to the list
            verticalArrangement = Arrangement.spacedBy(12.dp),  // Spacing between items
            contentPadding = PaddingValues(bottom = 16.dp)  // Padding at the bottom for scroll
        ) {
            items(filteredBirds) { bird ->
                GamifiedBirdCard(bird)
            }
        }

        // Add the Bottom Navigation Bar here as well
        BottomNavigationBar(navController, selectedTab = 2) { newIndex ->
            when (newIndex) {
                0 -> navController.navigate("homepage")
                1 -> navController.navigate("cameraalt")
                2 -> { /* Already in gamified list */ }
                3 -> navController.navigate("settings")  // Navigate to Settings
            }
        }
    }
}

@Composable
fun GamifiedBirdCard(bird: GamifiedBird) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)  // Adding some shadow for depth
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp) // Padding inside the card
        ) {
            Image(
                painter = painterResource(id = bird.imageRes),
                contentDescription = bird.name,
                modifier = Modifier
                    .size(70.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp)) // Spacing between image and text

            Column {
                Text(
                    text = bird.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Points: ${bird.points}",
                    fontSize = 14.sp,
                    color = Color(0xFF388E3C)
                )
            }
        }
    }
}
