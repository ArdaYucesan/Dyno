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

## Future Enhancements

- [ ] Remote debugging over network
- [ ] Parameter presets/configurations
- [ ] Real-time value monitoring/graphs
- [ ] Integration with existing debug tools
- [ ] Performance optimization
- [ ] CI/CD pipeline for library publishing