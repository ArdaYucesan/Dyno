package com.ardayucesan.dyno.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ardayucesan.dyno.Dyno
import com.ardayucesan.dyno.annotations.DynoFlow
import com.ardayucesan.dyno.annotations.DynoGroup
import com.ardayucesan.dyno.annotations.DynoTrigger
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
        enumMapping = [
            "bookingStatus:1:CREATED",
            "bookingStatus:2:WAITING_FOR_DRIVER_APPROVE",
            "bookingStatus:3:NO_AVAILABLE_DRIVER",
            "bookingStatus:4:CONVERTED_TO_TRIP",
            "bookingStatus:5:CANCEL_BY_PASSENGER",
            "tripStatus:1:ACTIVE",
            "tripStatus:2:COMPLETE",
            "tripStatus:3:INCOMPLETE_PROCESS",
            "tripStatus:4:IN_PAYMENT_PROCESS"
        ]
    )
    private val _passengerInfoFlow = MutableStateFlow(PassengerInfoModel())
    val passengerInfoFlow: StateFlow<PassengerInfoModel> = _passengerInfoFlow.asStateFlow()
    
    @DynoTrigger(
        name = "Update StateFlow",
        group = "Test Functions",
        description = "Manually update StateFlow with test values"
    )
    fun updateStateFlowValues() {
        _passengerInfoFlow.value = PassengerInfoModel(
            hasBooking = true,
            bookingStatus = 2,
            hasTrip = false,
            tripStatus = 1,
            isPackage = true
        )
        android.util.Log.d("MainActivity", "StateFlow manually updated to: ${_passengerInfoFlow.value}")
    }
    
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
    val activity = LocalContext.current as MainActivity
    val passengerInfo by activity.passengerInfoFlow.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🔧 Dyno StateFlow Test",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "📊 Current StateFlow Values:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                PassengerInfoRow("Has Booking", passengerInfo.hasBooking.toString())
                PassengerInfoRow("Booking Status", "${passengerInfo.bookingStatus}")
                PassengerInfoRow("Has Trip", passengerInfo.hasTrip.toString())
                PassengerInfoRow("Trip Status", "${passengerInfo.tripStatus}")
                PassengerInfoRow("Is Package", passengerInfo.isPackage.toString())
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Instructions:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "1. Open Dyno debug interface from notification\n2. Go to 'Flow Manipulations' section\n3. Change 'Passenger Info' field values\n4. Watch values update here in real-time!",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Button(
            onClick = { 
                Dyno.launchDebugInterface(activity)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🔧 Open Dyno Debug Interface")
        }
    }
}

@Composable
fun PassengerInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}