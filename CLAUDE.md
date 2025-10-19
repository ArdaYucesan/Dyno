# Dyno - Android Debug Tool Library

## Project Overview

Dyno is an Android debugging library that allows developers to modify UI state parameters and trigger methods in real-time without rebuilding the app. It provides a Unity Inspector-like experience for Android development.

## Architecture

The project follows a multi-module Android library structure:

```
dyno-groom/
├── dyno-annotations/          # Pure Kotlin module with annotation definitions
├── dyno-processor/           # KSP annotation processor for code generation
├── dyno-runtime/            # Android library for core parameter management
├── dyno-ui/                # Android library with Compose debug interface
├── dyno-core/              # Main API and initialization (depends on all modules)
├── sample-app/             # Demo application showing library usage
└── app/                    # Original empty app module
```

## Key Features

1. **Annotation-based parameter exposure** - `@DynoExpose` for fields, `@DynoTrigger` for methods
2. **Real-time parameter modification** - Changes apply immediately without rebuilds
3. **Notification-based access** - Persistent notification like Chucker/Pluto
4. **Compose UI** - Modern debug interface built with Jetpack Compose
5. **Type safety** - Supports Boolean, Int, Long, Float, Double, String, Enum
6. **Grouping system** - Organize parameters by logical groups
7. **Debug-only operation** - Automatically disabled in release builds

## Build Commands

```bash
# Build the entire project
./gradlew build

# Build specific modules
./gradlew :dyno-core:build
./gradlew :sample-app:build

# Run the sample app
./gradlew :sample-app:installDebug

# Clean build
./gradlew clean build
```

## Development Workflow

1. **Core library development**: Work on dyno-* modules
2. **Testing**: Use sample-app to test functionality
3. **Documentation**: Update README.md and code comments
4. **Versioning**: Follow semantic versioning for releases

## Module Dependencies

```
dyno-core (API module)
├── dyno-annotations
├── dyno-runtime
│   └── dyno-annotations
├── dyno-ui
│   ├── dyno-annotations
│   └── dyno-runtime
└── (exposes all as API dependencies)

sample-app
├── dyno-core
└── dyno-processor (for KSP)
```

## Current Status

✅ Multi-module structure created
✅ Annotation system implemented
✅ KSP processor working
✅ Runtime parameter management
✅ Compose UI interface
✅ Core API module
✅ Sample app with ButtonManager example
✅ Documentation and README
✅ @DynoFlow annotation system implemented
✅ StateFlow data class field manipulation
✅ Integrated with Marti Android project

## Usage Example

```kotlin
@DynoGroup(name = "Button Manager")
class ButtonManager {
    @DynoExpose(name = "Has Trip", group = "Trip Status")
    var hasTrip: Boolean = false
    
    @DynoTrigger(name = "Update UI", group = "Actions")
    fun updateButtonStates() {
        // UI update logic
    }
}

// In Activity
Dyno.initialize(this)
val buttonManager = ButtonManager()
Dyno.register(buttonManager)
```

## Technologies Used

- **Kotlin** - Primary language
- **KSP** - Annotation processing
- **Jetpack Compose** - UI framework
- **Android Architecture Components** - LiveData, ViewModel patterns
- **Reflection** - Runtime parameter access
- **Gradle Version Catalogs** - Dependency management

---

## @DynoFlow Implementation (2025-10-19)

### Problem Solved
Previously, debugging StateFlow data class fields required ugly boilerplate code with separate override variables and manual switches. This created messy debug code that cluttered ViewModels.

### Solution: @DynoFlow Annotation
Created a new `@DynoFlow` annotation that allows direct manipulation of StateFlow data class fields without separate override variables.

### Technical Implementation

#### 1. New Annotation
```kotlin
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class DynoFlow(
    val name: String = "",
    val group: String = "Default",
    val description: String = "",
    val fields: Array<String> = []
)
```

#### 2. Data Classes Added
- `DynoFlowField` - Represents manipulatable field within data class
- `DynoFlowManipulation` - Metadata for StateFlow manipulation

#### 3. KSP Processor Enhancement
- Added processing of `@DynoFlow` annotations
- Generates `registerFlowManipulation()` calls in DynoRegistry

#### 4. Runtime Registry Updates
- `registerFlowManipulation()` - Register StateFlow for manipulation
- `setFlowFieldOverride()` - Set override values for specific fields
- `manipulateStateFlow()` - Apply overrides by creating new data class instances
- `copyDataClassWithOverrides()` - Create data class copies with field overrides

#### 5. UI Components
- `DynoFlowManipulationRow` - Expandable card for each StateFlow
- `DynoFlowFieldRow` - Individual field manipulation controls

### Marti Project Integration

#### Before (Ugly Override Code)
```kotlin
// Separate debug variables cluttering the ViewModel
var bookingStatusOverride: Int = -1
var tripStatusOverride: Int = -1
var searchAgainOverride: Boolean? = null

// Manual override logic in getters
fun bookingStatus(): Int? = if (bookingStatusOverride != -1) 
    bookingStatusOverride else passengerInfoFlow.value?.bookingStatus
```

#### After (@DynoFlow Clean Solution)
```kotlin
@DynoFlow(
    name = "Passenger Info",
    group = "Trip States",
    description = "Debug passenger booking and trip status information",
    fields = ["hasBooking", "bookingStatus", "hasTrip", "tripStatus", "isSearchingAgain", "isInPaymentProcess"]
)
private val _passengerInfoFlow = MutableStateFlow<PassengerInfoModel?>(null)
val passengerInfoFlow: StateFlow<PassengerInfoModel?> = _passengerInfoFlow.asStateFlow()

// No override variables needed! Direct StateFlow manipulation
```

### Files Modified
- `/Users/arda_yucesan/AndroidStudioProjects/Dyno/dyno-annotations/src/main/kotlin/com/ardayucesan/dyno/annotations/DynoFlow.kt` (NEW)
- `/Users/arda_yucesan/AndroidStudioProjects/Dyno/dyno-annotations/src/main/kotlin/com/ardayucesan/dyno/annotations/DynoTypes.kt` (Updated)
- `/Users/arda_yucesan/AndroidStudioProjects/Dyno/dyno-processor/src/main/kotlin/com/ardayucesan/dyno/processor/DynoSymbolProcessor.kt` (Updated)
- `/Users/arda_yucesan/AndroidStudioProjects/Dyno/dyno-runtime/src/main/kotlin/com/ardayucesan/dyno/runtime/DynoParameterRegistry.kt` (Enhanced)
- `/Users/arda_yucesan/AndroidStudioProjects/Dyno/dyno-ui/src/main/kotlin/com/ardayucesan/dyno/ui/DynoDebugActivity.kt` (UI Components Added)
- `/Users/arda_yucesan/Projects/ProjectsLookup/marti.android/feature/presentation/src/main/java/com/martitech/main/presentation/MainVM.kt` (Applied @DynoFlow)

### Build & Deployment
✅ Successfully built new AAR files with @DynoFlow support
✅ Copied updated libraries to Marti app: `/Users/arda_yucesan/Desktop/app/libs/`
- `dyno-core-release.aar`
- `dyno-runtime-release.aar` 
- `dyno-ui-release.aar`
- `dyno-annotations.jar`
- `dyno-processor.jar`

### Test Status
⚠️ **Pending Testing** - Backend was down in dev environment, will test functionality later

### Benefits Achieved
- ✅ **Eliminated boilerplate code** - No more override variables
- ✅ **Clean ViewModel code** - No debug clutter in production logic  
- ✅ **Direct StateFlow manipulation** - Real-time field modification
- ✅ **Type-safe field editing** - UI adapts to field types automatically
- ✅ **Expandable interface** - Clean, organized debug UI

---

## Future Enhancements

- [ ] Remote debugging over network
- [ ] Parameter presets/configurations
- [ ] Real-time value monitoring/graphs
- [ ] Integration with existing debug tools
- [ ] Performance optimization
- [ ] CI/CD pipeline for library publishing
- [ ] Test @DynoFlow functionality when dev backend is available