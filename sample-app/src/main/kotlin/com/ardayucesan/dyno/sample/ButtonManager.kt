package com.ardayucesan.dyno.sample

import com.ardayucesan.dyno.annotations.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Sample UserState data class for testing @DynoFlow functionality
 */
data class UserState(
    val isLoggedIn: Boolean = false,
    val userName: String = "",
    val userLevel: Int = 1,
    val hasActiveSubscription: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val currentCity: String = "Istanbul"
)

/**
 * Sample ButtonManager that manages domain buttons like Martı Tag, Scooter, and Courier buttons.
 * This demonstrates how to use Dyno annotations for runtime parameter modification.
 */
@DynoGroup(
    name = "Button Manager",
    description = "Manages the state and appearance of domain buttons"
)
class ButtonManager {
    
    // @DynoFlow example - StateFlow with manipulatable data class fields
    @field:DynoFlow(
        name = "User State",
        group = "User Management",
        description = "Current user state information that can be manipulated for testing",
        fields = ["isLoggedIn", "userName", "userLevel", "hasActiveSubscription", "notificationsEnabled", "currentCity"]
    )
    private val _userStateFlow = MutableStateFlow(UserState())
    val userStateFlow: StateFlow<UserState> = _userStateFlow.asStateFlow()
    
    init {
        // Log StateFlow changes for debugging
        android.util.Log.d("ButtonManager", "ButtonManager initialized with UserState: ${_userStateFlow.value}")
    }
    
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
    
    @DynoTrigger(
        name = "Update User State",
        group = "User Management",
        description = "Update user state with some test values"
    )
    fun updateUserState() {
        _userStateFlow.value = UserState(
            isLoggedIn = true,
            userName = "Test User",
            userLevel = 5,
            hasActiveSubscription = true,
            notificationsEnabled = true,
            currentCity = "Ankara"
        )
        android.util.Log.d("ButtonManager", "User state updated: ${_userStateFlow.value}")
    }
    
    @DynoTrigger(
        name = "Log Current User State",
        group = "User Management", 
        description = "Log the current user state to console"
    )
    fun logCurrentUserState() {
        val currentState = _userStateFlow.value
        val publicState = userStateFlow.value
        android.util.Log.d("ButtonManager", "=== Current User State ===")
        android.util.Log.d("ButtonManager", "Private flow value: $currentState")
        android.util.Log.d("ButtonManager", "Public flow value: $publicState")
        android.util.Log.d("ButtonManager", "Is Logged In: ${currentState.isLoggedIn}")
        android.util.Log.d("ButtonManager", "User Name: '${currentState.userName}'")
        android.util.Log.d("ButtonManager", "User Level: ${currentState.userLevel}")
        android.util.Log.d("ButtonManager", "Has Subscription: ${currentState.hasActiveSubscription}")
        android.util.Log.d("ButtonManager", "Notifications: ${currentState.notificationsEnabled}")
        android.util.Log.d("ButtonManager", "Current City: '${currentState.currentCity}'")
        android.util.Log.d("ButtonManager", "========================")
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