package com.example.birdapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.birdapp.ui.theme.BIRDAPPTheme
import com.example.birdapp.utils.SessionUtils
import com.google.firebase.auth.FirebaseAuth
import com.example.birdapp.AchievementScreen // ✅ Make sure this import is present

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔐 Request runtime permissions
        requestPermissionsIfNeeded()

        setContent {
            BIRDAPPTheme {
                val navController = rememberNavController()

                val isUserLoggedIn = FirebaseAuth.getInstance().currentUser != null
                val isFirstLaunch = SessionUtils.isFirstLaunch(this)

                val startDestination = when {
                    isFirstLaunch -> "welcomePage"
                    isUserLoggedIn -> "homepage"
                    else -> "welcomeScreen"
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("welcomeScreen") {
                            WelcomeScreen(navController, this@MainActivity)
                        }
                        composable("welcomePage") {
                            SessionUtils.setFirstLaunchDone(this@MainActivity)
                            WelcomePage(navController, this@MainActivity)
                        }
                        composable("loginsignup") { LoginSignupScreen(navController) }
                        composable("login") { LoginScreen(navController) }
                        composable("signup") { SignupScreen(navController) }
                        composable("confirmationScreen") { ConfirmationScreen(navController) }
                        composable("homepage") { HomePage(navController) }
                        composable("cameraalt") { CameraAltScreen(navController) }
                        composable("book") { BookScreen(navController) }

                        // ✅ Gamified List Screen (now shows achievements)
                        composable("gamifiedlist") {
                            AchievementScreen(navController)
                        }

                        // Settings and related screens
                        composable("settings") { SettingsScreen(navController) }
                        composable("profileSettings") { EditProfileScreen(navController) }
                        composable("notificationsSettings") { NotificationSettingsScreen(navController) }
                        composable("locationSettings") { LocationSettingsScreen(navController) }
                        composable("privacySettings") { PrivacySettingsScreen(navController) }
                        composable("aboutUs") { AboutUsScreen(navController) }

                        // Geolocation screen
                        composable("geolocation") { GeolocationScreen(navController) }
                    }
                }
            }
        }
    }

    private fun requestPermissionsIfNeeded() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 100)
        }
    }
}
