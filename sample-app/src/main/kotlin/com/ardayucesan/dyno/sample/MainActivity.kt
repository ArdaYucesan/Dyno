package com.ardayucesan.dyno.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ardayucesan.dyno.Dyno
import com.ardayucesan.dyno.annotations.DynoFlow
import com.ardayucesan.dyno.annotations.DynoGroup
import com.ardayucesan.dyno.sample.ui.theme.DynoSampleTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PassengerInfoModel(
    val hasBooking: Boolean = false,
    val bookingStatus: Int = 0,
    val hasTrip: Boolean = false,
    val tripStatus: Int = 0,
    val isPackage: Boolean = false
)

@DynoGroup(
    name = "MainActivity",
    description = "Main Activity UI Controls"
)
class MainActivity : ComponentActivity() {
    
    @field:DynoFlow(
        name = "Passenger Info",
        group = "Trip States",
        description = "Debug passenger booking and trip status information",
        fields = ["hasBooking", "bookingStatus", "hasTrip", "tripStatus", "isPackage"],
    )
    private val _passengerInfoFlow = MutableStateFlow(PassengerInfoModel())
    val passengerInfoFlow: StateFlow<PassengerInfoModel> = _passengerInfoFlow.asStateFlow()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Dyno
        Dyno.initialize(this, enableInDebug = true)
        
        // Register MainActivity itself
        Dyno.register(this)
        
        setContent {
            DynoSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Unregister when destroying
        Dyno.unregister(this)
    }
}

@Composable
fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hello World",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}