package com.aryntra.darpan

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * S1.3 Kotlin Domain Model Exercise.
 * Demonstrates: data class, val immutability, nullable types (String?).
 */
data class DeviceSnapshot(
    val timestamp: Long,
    val sampleLabel: String?,
    val sequenceNumber: Int
)

class MainActivity : ComponentActivity() {
    private val TAG = "DarpanLifecycle"
    private val currentLifecycleState = mutableStateOf("Created (onCreate)")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DarpanLearningDashboard(
                        lifecycleState = currentLifecycleState.value,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
        currentLifecycleState.value = "Started (onStart)"
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
        currentLifecycleState.value = "Resumed (onResume)"
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
        currentLifecycleState.value = "Paused (onPause)"
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
        currentLifecycleState.value = "Stopped (onStop)"
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
}

@Composable
fun DarpanLearningDashboard(
    lifecycleState: String,
    modifier: Modifier = Modifier
) {
    // S1.5 Context access via LocalContext
    val context = LocalContext.current
    val packageName = context.packageName
    val appName = context.applicationInfo.loadLabel(context.packageManager).toString()

    // S1.4 Compose State & Recomposition
    var refreshCount by remember { mutableIntStateOf(0) }
    var latestSnapshot by remember {
        mutableStateOf(
            DeviceSnapshot(
                timestamp = System.currentTimeMillis(),
                sampleLabel = "Initial Snapshot",
                sequenceNumber = 0
            )
        )
    }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "Aryntra Darpan",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Sprint S1 • Native Android Foundation",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            // Section 1: Android Lifecycle Observation
            InfoCard(title = "Android Lifecycle") {
                Text(
                    text = "Current Activity State:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Text(
                    text = lifecycleState,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Activity is managed by Android OS runtime.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Section 2: Android Context Exploration
            InfoCard(title = "Android Context") {
                Text(
                    text = "Application Label: ",
                    fontSize = 14.sp
                )
                Text(
                    text = "Package Name: ",
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Context bridges application components to Android OS.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Section 3: Compose State & Kotlin Model Interaction
            InfoCard(title = "Compose State & Kotlin Model") {
                Text(
                    text = "Interaction Count: ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                val formattedTime = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
                    .format(Date(latestSnapshot.timestamp))
                
                // Safe nullable unwrap demonstration (?: elvis operator)
                val labelText = latestSnapshot.sampleLabel ?: "Unnamed Sample"
                
                Text(
                    text = "Latest Snapshot [#]:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "• Label: \n• Timestamp: ",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        refreshCount++
                        latestSnapshot = DeviceSnapshot(
                            timestamp = System.currentTimeMillis(),
                            sampleLabel = "Snapshot Iteration ",
                            sequenceNumber = refreshCount
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Trigger State Change & Snapshot")
                }
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}
