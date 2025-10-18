package com.ardayucesan.dyno.sample

import com.ardayucesan.dyno.annotations.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

/**
 * Sample ButtonManager that manages domain buttons like Martı Tag, Scooter, and Courier buttons.
 * This demonstrates how to use Dyno annotations for runtime parameter modification.
 */
@DynoGroup(
    name = "Button Manager",
    description = "Manages the state and appearance of domain buttons"
)
class ButtonManager {
    
    // Trip related parameters
    @field:DynoExpose(
        group = "Trip Status",
        description = "Whether user has an active trip"
    )
    var hasTrip: Boolean = false

    @field:DynoExpose(
        group = "Trip Status",
        description = "Current trip status (0=None, 1=Active, 2=Paused, 3=Completed)",
        enumMapping = ["0:None", "1:Active", "2:Paused", "3:Completed"]
    )
    var tripStatus: Int = 0
    
    // Booking related parameters
    @field:DynoExpose(
        name = "Has Active Booking",
        group = "Booking Status",
        description = "Whether user has an active booking"
    )
    var hasBooking: Boolean = false
    
    @field:DynoExpose(
        name = "Booking Status",
        group = "Booking Status",
        description = "Current booking status (0=None, 1=Reserved, 2=Confirmed, 3=InProgress)",
        enumMapping = ["0:None", "1:Reserved", "2:Confirmed", "3:InProgress"]
    )
    var bookingStatus: Int = 0
    
    // Courier related parameters
    @field:DynoExpose(
        name = "Has Active Courier",
        group = "Courier Status",
        description = "Whether user has an active courier delivery"
    )
    var hasCourier: Boolean = false
    
    @field:DynoExpose(
        name = "Courier Status",
        group = "Courier Status",
        description = "Current courier status (0=None, 1=Searching, 2=Assigned, 3=Delivering)",
        enumMapping = ["0:None", "1:Searching", "2:Assigned", "3:Delivering"]
    )
    var courierStatus: Int = 0
    
    @field:DynoExpose(
        name = "Button Theme",
        group = "UI Settings",
        description = "Button color theme"
    )
    var buttonTheme: ButtonTheme = ButtonTheme.DEFAULT
    
    @field:DynoExpose(
        name = "Animation Duration",
        group = "UI Settings",
        description = "Button animation duration in milliseconds",
        min = 100.0,
        max = 2000.0,
        step = 100.0
    )
    var animationDuration: Int = 300
    
    // State calculation methods
    @DynoTrigger(
        group = "Triggers",
    )
    fun updateButtonStates() {
        android.util.Log.d("ButtonManager", "Updating button states...")
        android.util.Log.d("ButtonManager", "Trip: hasTrip=$hasTrip, status=$tripStatus")
        android.util.Log.d("ButtonManager", "Booking: hasBooking=$hasBooking, status=$bookingStatus")
        android.util.Log.d("ButtonManager", "Courier: hasCourier=$hasCourier, status=$courierStatus")
        
        // Simulate UI update
        updateTagButton()
        updateScooterButton()
        updateCourierButton()
    }
    
    @DynoTrigger(
        group = "Triggers",
    )
    fun resetAllStates() {
        hasTrip = false
        tripStatus = 0
        hasBooking = false
        bookingStatus = 0
        hasCourier = false
        courierStatus = 0
        buttonTheme = ButtonTheme.DEFAULT
        animationDuration = 300
        
        android.util.Log.d("ButtonManager", "All states reset to default")
        updateButtonStates()
    }
    
    @DynoTrigger(
        group = "Triggers",
    )
    fun simulateActiveTrip() {
        hasTrip = true
        tripStatus = 1
        hasBooking = false
        bookingStatus = 0
        hasCourier = false
        courierStatus = 0
        
        android.util.Log.d("ButtonManager", "Simulating active trip")
        updateButtonStates()
    }
    
    @DynoTrigger(
        group = "Triggers",
    )
    fun simulateActiveBooking() {
        hasTrip = false
        tripStatus = 0
        hasBooking = true
        bookingStatus = 2
        hasCourier = false
        courierStatus = 0
        
        android.util.Log.d("ButtonManager", "Simulating active booking")
        updateButtonStates()
    }
    
    @DynoTrigger(
        group = "Triggers",
    )
    fun simulateCourierDelivery() {
        hasTrip = false
        tripStatus = 0
        hasBooking = false
        bookingStatus = 0
        hasCourier = true
        courierStatus = 3
        
        android.util.Log.d("ButtonManager", "Simulating courier delivery")
        updateButtonStates()
    }
    
    // Private helper methods
    private fun updateTagButton() {
        val color = when {
            hasTrip && tripStatus == 1 -> "GREEN" // Active trip
            hasBooking && bookingStatus > 0 -> "BLUE" // Has booking
            else -> "DEFAULT"
        }
        android.util.Log.d("ButtonManager", "Tag button color: $color")
    }
    
    private fun updateScooterButton() {
        val color = when {
            hasTrip && tripStatus == 1 -> "GREEN" // Active trip
            hasBooking && bookingStatus > 0 -> "BLUE" // Has booking
            else -> "DEFAULT"
        }
        android.util.Log.d("ButtonManager", "Scooter button color: $color")
    }
    
    private fun updateCourierButton() {
        val color = when {
            hasCourier && courierStatus > 0 -> "ORANGE" // Active courier
            else -> "DEFAULT"
        }
        android.util.Log.d("ButtonManager", "Courier button color: $color")
    }
    
    enum class ButtonTheme {
        DEFAULT,
        DARK,
        LIGHT,
        COLORFUL
    }
}