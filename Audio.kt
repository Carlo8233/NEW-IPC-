package com.example.birdapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.Color

fun hasAudioPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun AudioRecordScreen(navController: NavHostController) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var recordButtonText by remember { mutableStateOf("Start Recording") }

    val audioFilePath = remember {
        "${context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath}/bird_recording.m4a"
    }

    var mediaRecorder: MediaRecorder? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (!hasAudioPermission(context)) {
                    Toast.makeText(context, "Please allow microphone permission first", Toast.LENGTH_LONG).show()
                    return@Button
                }

                if (!isRecording) {
                    requestAudioPermissions(context)
                    try {
                        mediaRecorder = MediaRecorder().apply {
                            setAudioSource(MediaRecorder.AudioSource.MIC)
                            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                            setOutputFile(audioFilePath)
                            prepare()
                            start()
                        }
                        isRecording = true
                        recordButtonText = "Stop Recording"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Recording failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    try {
                        mediaRecorder?.apply {
                            stop()
                            release()
                        }
                        mediaRecorder = null
                        isRecording = false
                        recordButtonText = "Start Recording"

                        Toast.makeText(context, "Audio saved to:\n$audioFilePath", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Stopping failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text(recordButtonText, color = Color.White)
        }
    }
}

fun requestAudioPermissions(context: android.content.Context) {
    val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    ActivityCompat.requestPermissions(
        context as android.app.Activity,
        permissions,
        200
    )
}
