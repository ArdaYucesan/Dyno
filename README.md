# 🔧 Dyno - Android Debug Tool Library

Dyno is a powerful Android debugging library that allows you to modify UI state parameters and trigger methods in real-time without rebuilding your app. Think of it as Unity Inspector for Android development.

## ✨ Features

- 🔄 **Real-time parameter modification** - Change variables instantly without rebuilding
- 🎯 **Annotation-based** - Minimal boilerplate with simple annotations
- 🔔 **Notification access** - Quick access via persistent notification (like Chucker/Pluto)
- 🎨 **Beautiful UI** - Modern Compose-based debug interface
- 📱 **Type support** - Boolean, Int, Long, Float, Double, String, Enum
- 🏷️ **Grouping** - Organize parameters by groups for better UX
- ⚡ **Method triggers** - Execute methods from debug interface
- 🌊 **StateFlow manipulation** - Direct data class field editing with @DynoFlow
- 🛡️ **Debug-only** - Automatically disabled in release builds

## 🚀 Quick Start

### 1. Add Dyno to your project

Add to your app-level `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.ardayucesan.dyno:dyno-core:1.0.0")
    ksp("com.ardayucesan.dyno:dyno-processor:1.0.0")
}

plugins {
    id("com.google.devtools.ksp") version "2.0.21-1.0.28"
}
```

### 2. Initialize Dyno

In your `Application` or `MainActivity`:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Dyno (only active in debug builds)
        Dyno.initialize(this, enableInDebug = BuildConfig.DEBUG)
    }
}
```

### 3. Annotate your classes

```kotlin
@DynoGroup(name = "Button Manager", description = "Manages UI button states")
class ButtonManager {
    
    @DynoExpose(
        name = "Has Active Trip",
        group = "Trip Status",
        description = "Whether user has an active trip"
    )
    var hasTrip: Boolean = false
    
    @DynoExpose(
        name = "Trip Status",
        group = "Trip Status", 
        description = "Current trip status (0=None, 1=Active, 2=Paused)"
    )
    var tripStatus: Int = 0
    
    @DynoTrigger(
        name = "Update UI",
        group = "Actions",
        description = "Refresh button states"
    )
    fun updateButtonStates() {
        // Your UI update logic
    }
}
```

### 4. Register your instances

```kotlin
class MainActivity : ComponentActivity() {
    private val buttonManager = ButtonManager()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Register instance for debugging
        Dyno.register(buttonManager)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Dyno.unregister(buttonManager)
    }
}
```

### 5. Access debug interface

- Check your notification panel for "🔧 Dyno Debug Active"
- Tap the notification to open the debug interface
- Or call `Dyno.launchDebugInterface(context)` programmatically

## 📖 Documentation

### Annotations

#### `@DynoGroup`
Marks a class for Dyno monitoring:
```kotlin
@DynoGroup(
    name = "Custom Name",           // Display name (optional)
    description = "Description",    // Description text (optional)
    enabled = true                  // Enable by default (optional)
)
class MyClass { }
```

#### `@DynoExpose`
Exposes a field for runtime modification:
```kotlin
@DynoExpose(
    name = "Display Name",          // UI display name (optional)
    group = "Group Name",           // Group for organization (optional)
    description = "Description",    // Help text (optional)
    min = 0.0,                     // Minimum value for numbers (optional)
    max = 100.0,                   // Maximum value for numbers (optional)
    step = 1.0                     // Step size for sliders (optional)
)
var myParameter: Int = 50
```

#### `@DynoTrigger`
Marks a method to be triggerable from UI:
```kotlin
@DynoTrigger(
    name = "Action Name",           // UI display name (optional)
    group = "Group Name",           // Group for organization (optional)
    description = "Description"     // Help text (optional)
)
fun myAction() { }
```

#### `@DynoFlow` ⭐ NEW!
Enables direct manipulation of StateFlow data class fields:
```kotlin
@DynoFlow(
    name = "User State",            // UI display name (optional)
    group = "User Management",      // Group for organization (optional)
    description = "Current user state", // Help text (optional)
    fields = ["isLoggedIn", "userName", "userLevel"] // Fields to expose
)
private val _userStateFlow = MutableStateFlow(UserState())
val userStateFlow: StateFlow<UserState> = _userStateFlow.asStateFlow()
```

**Benefits of @DynoFlow:**
- ✅ **No boilerplate** - Eliminate separate override variables
- ✅ **Clean ViewModels** - No debug clutter in production code
- ✅ **Real-time manipulation** - Direct StateFlow field editing
- ✅ **Type-safe** - Automatic field type detection and conversion

### Supported Types

- `Boolean` - Toggle switch
- `Int` - Number input field
- `Long` - Number input field  
- `Float` - Decimal input field
- `Double` - Decimal input field
- `String` - Text input field
- `Enum` - Dropdown selection

### API Reference

#### Dyno Class
```kotlin
// Initialize Dyno
Dyno.initialize(context, enableInDebug = true, autoShowNotification = true)

// Register/unregister instances
Dyno.register(instance)
Dyno.unregister(instance)

// Show/hide debug interface
Dyno.showDebugNotification(context)
Dyno.hideDebugNotification(context)
Dyno.launchDebugInterface(context)

// Check status
Dyno.isInitialized()
Dyno.isDebugMode()
```

#### Extension Functions
```kotlin
// Alternative registration methods
myObject.dynoRegister()
myObject.dynoUnregister()
```

## 🏗️ Architecture

Dyno is built with a multi-module architecture:

- **dyno-annotations** - Annotation definitions
- **dyno-processor** - KSP annotation processor  
- **dyno-runtime** - Core parameter management
- **dyno-ui** - Compose-based debug interface
- **dyno-core** - Main API and initialization
- **sample-app** - Example implementation

## 🌊 StateFlow Debugging with @DynoFlow

### The Problem with Traditional Debugging
Before @DynoFlow, debugging StateFlow data class fields required ugly boilerplate:

```kotlin
// ❌ UGLY: Separate override variables cluttering ViewModel
var bookingStatusOverride: Int = -1
var tripStatusOverride: Int = -1
var searchAgainOverride: Boolean? = null

// ❌ UGLY: Manual override logic in getters
fun bookingStatus(): Int? = if (bookingStatusOverride != -1) 
    bookingStatusOverride else passengerInfoFlow.value?.bookingStatus
```

### The @DynoFlow Solution
With @DynoFlow, achieve clean, direct StateFlow manipulation:

```kotlin
// ✅ CLEAN: Single annotation, no boilerplate
@field:DynoFlow(
    name = "Passenger Info",
    group = "Trip States",
    description = "Debug passenger booking and trip status",
    fields = ["hasBooking", "bookingStatus", "hasTrip", "tripStatus"]
)
private val _passengerInfoFlow = MutableStateFlow<PassengerInfoModel?>(null)
val passengerInfoFlow: StateFlow<PassengerInfoModel?> = _passengerInfoFlow.asStateFlow()

// ✅ CLEAN: No override variables needed!
// StateFlow manipulation happens automatically via Dyno UI
```

### How It Works
1. **Annotation Processing** - KSP processor detects @DynoFlow annotations
2. **Runtime Registration** - Dyno registers StateFlow fields for manipulation
3. **UI Generation** - Debug interface shows expandable cards with field controls
4. **Data Class Copying** - Uses Kotlin reflection to create new instances with overrides
5. **StateFlow Update** - Automatically updates StateFlow with new data class instance

### Requirements
- Must use `@field:DynoFlow` prefix for backing field annotation
- StateFlow must contain data class (not primitives)
- Specify field names in `fields` array parameter

## 🔧 Advanced Usage

### Custom Application Class

Extend `DynoApplication` for automatic initialization:

```kotlin
class MyApplication : DynoApplication() {
    override fun onCreate() {
        super.onCreate() // Automatically initializes Dyno
        // Your app initialization
    }
}
```

### Manual Parameter Control

Use `DynoUtils` for programmatic access:

```kotlin
// Get current parameter value
val value = DynoUtils.getParameterValue("com.example.ButtonManager", "hasTrip")

// Set parameter value
DynoUtils.setParameterValue("com.example.ButtonManager", "hasTrip", true)

// Trigger method
DynoUtils.triggerMethod("com.example.ButtonManager", "updateButtonStates")
```

### ProGuard Configuration

Dyno includes consumer ProGuard rules, but if needed:

```proguard
# Keep Dyno annotations
-keep @interface com.ardayucesan.dyno.annotations.** { *; }
-keepclassmembers class * {
    @com.ardayucesan.dyno.annotations.DynoExpose *;
    @com.ardayucesan.dyno.annotations.DynoTrigger *;
}
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Inspired by Unity Inspector
- Built with Jetpack Compose
- Uses KSP for annotation processing

---

**Made with ❤️ for Android developers who want to debug UI states efficiently**