package com.ardayucesan.dyno.sample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ardayucesan.dyno.Dyno
import com.ardayucesan.dyno.annotations.DynoTrigger
import com.ardayucesan.dyno.annotations.DynoGroup
import com.ardayucesan.dyno.sample.ui.theme.DynoSampleTheme

@DynoGroup(
    name = "MainActivity",
    description = "Main Activity UI Controls"
)
class MainActivity : ComponentActivity() {
    
    private lateinit var buttonManager: ButtonManager
    private var recomposeState by mutableStateOf(0)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Dyno
        Dyno.initialize(this, enableInDebug = true)
        
        // Create and register ButtonManager
        buttonManager = ButtonManager()
        Dyno.register(buttonManager)
        
        // Register MainActivity itself for UI refresh trigger
        Dyno.register(this)
        
        // Debug: Manual registration to test
        android.util.Log.d("MainActivity", "ButtonManager created and registered")
        android.util.Log.d("MainActivity", "ButtonManager hasTrip: ${buttonManager.hasTrip}")
        
        // Trigger initial state to test
        buttonManager.updateButtonStates()
        
        setContent {
            DynoSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(buttonManager, recomposeState)
                }
            }
        }
    }
    
    @DynoTrigger(
        name = "Refresh UI",
        group = "Triggers", 
        description = "Force refresh the UI to reflect parameter changes"
    )
    fun refreshUI() {
        android.util.Log.d("MainActivity", "Refreshing UI...")
        recomposeState++
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Unregister when destroying
        Dyno.unregister(buttonManager)
        Dyno.unregister(this)
    }
}

@Composable
fun MainScreen(buttonManager: ButtonManager, recomposeState: Int = 0) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        
        // Header
        Text(
            text = "🔧 Dyno Sample App",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Text(
            text = "This app demonstrates Dyno debugging library",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Check the notification panel for Dyno debug interface",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Domain buttons (simulated)
        Text(
            text = "Domain Buttons:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Tag Button
        Log.d("ButtonManager", "MainScreen: recompose $recomposeState")
        DomainButton(
            text = "Martı TAG",
            color = getTagButtonColor(buttonManager),
            onClick = { /* Handle tag button click */ }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Scooter Button
        DomainButton(
            text = "Martı Scooter",
            color = getScooterButtonColor(buttonManager),
            onClick = { /* Handle scooter button click */ }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Courier Button
        DomainButton(
            text = "Martı Courier",
            color = getCourierButtonColor(buttonManager),
            onClick = { /* Handle courier button click */ }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Debug info
        if (true) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Debug Info:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Trip: ${buttonManager.hasTrip} (${buttonManager.tripStatus})")
                    Text("Booking: ${buttonManager.hasBooking} (${buttonManager.bookingStatus})")
                    Text("Courier: ${buttonManager.hasCourier} (${buttonManager.courierStatus})")
                    Text("Theme: ${buttonManager.buttonTheme}")
                    Text("Animation: ${buttonManager.animationDuration}ms")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Manual debug interface launcher
        val context = LocalContext.current
        Button(
            onClick = { 
                Dyno.launchDebugInterface(context)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🔧 Open Dyno Debug Interface")
        }
    }
}

@Composable
fun DomainButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun getTagButtonColor(buttonManager: ButtonManager): Color {
    Log.d("ButtonManager", "getTagButtonColor: worked ${buttonManager.hasTrip}")
    Log.d("ButtonManager", "getTagButtonColor: worked ${buttonManager.hasBooking}")
    Log.d("ButtonManager", "getTagButtonColor: worked ${buttonManager.bookingStatus}")
    return when {
        buttonManager.hasTrip && buttonManager.tripStatus == 1 -> Color(0xFF4CAF50) // Green
        buttonManager.hasBooking && buttonManager.bookingStatus > 0 -> Color(0xFF2196F3) // Blue
        else -> Color(0xFF9E9E9E) // Gray
    }
}

@Composable
fun getScooterButtonColor(buttonManager: ButtonManager): Color {
    Log.d("ButtonManager", "getScooterButtonColor: worked ${buttonManager.hasTrip}")
    Log.d("ButtonManager", "getScooterButtonColor: worked ${buttonManager.tripStatus}")
    return when {
        buttonManager.hasTrip && buttonManager.tripStatus == 1 -> Color(0xFF4CAF50) // Green
        buttonManager.hasBooking && buttonManager.bookingStatus > 0 -> Color(0xFF2196F3) // Blue
        else -> Color(0xFF9E9E9E) // Gray
    }
}

@Composable
fun getCourierButtonColor(buttonManager: ButtonManager): Color {
    Log.d("ButtonManager", "getCourierButtonColor: worked ${buttonManager.hasCourier}")
    Log.d("ButtonManager", "getCourierButtonColor: worked ${buttonManager.courierStatus}")
    return when {
        buttonManager.hasCourier && buttonManager.courierStatus > 0 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFF9E9E9E) // Gray
    }
}