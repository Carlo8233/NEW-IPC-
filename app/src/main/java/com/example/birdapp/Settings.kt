package com.example.birdapp

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val profilePicsRef = storageRef.child("profile_pictures/$userId.jpg")

    var showExtraSettings by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    // Load saved profile image from Firestore
    LaunchedEffect(userId) {
        userId?.let {
            db.collection("users").document(it).get().addOnSuccessListener { document ->
                document.getString("profileImageUrl")?.let { url ->
                    profileImageUrl = url
                }
            }
        }
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && userId != null) {
            // Upload image to Firebase Storage
            val uploadTask = profilePicsRef.putFile(uri)
            uploadTask.addOnSuccessListener {
                profilePicsRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    profileImageUrl = imageUrl
                    // Save URL to Firestore
                    db.collection("users").document(userId)
                        .update("profileImageUrl", imageUrl)
                    Toast.makeText(context, "Profile picture updated", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding() + 16.dp,
                bottom = 0.dp,
                start = 16.dp,
                end = 16.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Profile Picture
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable { showDialog = true },
                contentAlignment = Alignment.Center
            ) {
                val painter = if (profileImageUrl != null) {
                    rememberAsyncImagePainter(profileImageUrl)
                } else {
                    painterResource(id = R.drawable.profile)
                }

                Image(
                    painter = painter,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Change Profile Picture?") },
                    text = { Text("Are you sure you want to change your profile picture?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialog = false
                            imagePickerLauncher.launch("image/*")
                        }) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("No")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Username & Email
            Text(
                text = user?.displayName ?: "USERNAME",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = user?.email ?: "UserEmail@gmail.com",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Edit Profile Button
            Button(
                onClick = { showExtraSettings = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp)
            ) {
                Text(text = "EDIT PROFILE", fontSize = 16.sp, color = Color.Black)
            }

            if (showExtraSettings) {
                Spacer(modifier = Modifier.height(20.dp))

                SettingItem(title = "Edit Profile Info") {
                    navController.navigate("profileSettings")
                }
                SettingItem(title = "Notifications") {
                    navController.navigate("notificationsSettings")
                }
                SettingItem(title = "Location & Tracking") {
                    navController.navigate("locationSettings")
                }
                SettingItem(title = "Privacy & Security") {
                    navController.navigate("privacySettings")
                }
                SettingItem(title = "About Us") {
                    navController.navigate("aboutUs")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout Button
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9534F)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp)
            ) {
                Text(text = "LOGOUT", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        BottomNavigationBar(navController, selectedTab = 3) { newIndex ->
            when (newIndex) {
                0 -> navController.navigate("homepage")
                1 -> navController.navigate("cameraalt")
                2 -> navController.navigate("book")
                3 -> { /* Already in settings */ }
            }
        }
    }
}

@Composable
fun SettingItem(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 16.sp, color = Color.Black)
        }
    }
}
