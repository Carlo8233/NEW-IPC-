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

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    var unlocked: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    // Start progress at 0 so all achievements are initially locked
    var discoverCount by remember { mutableStateOf(0) }
    var appUsageDays by remember { mutableStateOf(0) }
    var viewedBirds by remember { mutableStateOf(0) }

    var showPopup by remember { mutableStateOf(false) }
    var justUnlockedAchievement by remember { mutableStateOf<Achievement?>(null) }

    val achievementStateList = remember {
        mutableStateListOf(
            Achievement("first", "First Discovery", "Discover your first bird!"),
            Achievement("explorer", "Explorer", "Use the app for 5 days."),
            Achievement("encyclopedia", "Bird Encyclopedia", "View 10 bird profiles.")
        )
    }

    // Unlock logic based on actual thresholds
    LaunchedEffect(discoverCount, appUsageDays, viewedBirds) {
        val updatedList = achievementStateList.map { achievement ->
            when (achievement.id) {
                "first" -> achievement.copy(unlocked = discoverCount >= 1)
                "explorer" -> achievement.copy(unlocked = appUsageDays >= 5)
                "encyclopedia" -> achievement.copy(unlocked = viewedBirds >= 10)
                else -> achievement
            }
        }

        updatedList.forEachIndexed { i, updated ->
            if (updated.unlocked && !achievementStateList[i].unlocked) {
                achievementStateList[i] = updated
                justUnlockedAchievement = updated
                showPopup = true
            } else {
                achievementStateList[i] = updated
            }
        }
    }

    val filteredAchievements = achievementStateList.filter {
        it.title.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { navController.navigate("homepage") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(top = 32.dp, bottom = 16.dp)
        ) {
            Text("← Back to Home", fontSize = 16.sp)
        }

        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Achievements") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFF1F1F1),
                focusedIndicatorColor = Color(0xFF388E3C),
                unfocusedIndicatorColor = Color(0xFF388E3C)
            )
        )

        Text("ACHIEVEMENTS", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("View All Your Achievements Here!", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(filteredAchievements) { achievement ->
                AchievementCard(achievement)
            }
        }

        BottomNavigationBar(navController, selectedTab = 2) { newIndex ->
            when (newIndex) {
                0 -> navController.navigate("homepage")
                1 -> navController.navigate("cameraalt")
                2 -> {}
                3 -> navController.navigate("settings")
            }
        }

        if (showPopup && justUnlockedAchievement != null) {
            AlertDialog(
                onDismissRequest = { showPopup = false },
                confirmButton = {
                    TextButton(onClick = { showPopup = false }) {
                        Text("Awesome!", fontWeight = FontWeight.Bold)
                    }
                },
                title = { Text("Achievement Unlocked!") },
                text = {
                    Column {
                        Text(justUnlockedAchievement!!.title, fontWeight = FontWeight.Bold)
                        Text(justUnlockedAchievement!!.description)
                    }
                }
            )
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    val backgroundColor = if (achievement.unlocked) Color(0xFFFFF9C4) else Color(0xFFDDDDDD)
    val titleColor = if (achievement.unlocked) Color.Black else Color.DarkGray
    val imageRes = if (achievement.unlocked) R.drawable.unlocked_achievement else R.drawable.locked_achievement

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = achievement.title,
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = if (achievement.unlocked) achievement.title else "Locked Achievement",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                if (achievement.unlocked) {
                    Text(achievement.description, fontSize = 14.sp, color = Color.DarkGray)
                }
            }
        }
    }
}
