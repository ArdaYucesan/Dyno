# Add project specific ProGuard rules here.
# Dyno Runtime ProGuard rules

# Keep all Dyno annotations
-keep @interface com.ardayucesan.dyno.annotations.** { *; }

# Keep annotated fields and methods
-keepclassmembers class * {
    @com.ardayucesan.dyno.annotations.DynoExpose *;
    @com.ardayucesan.dyno.annotations.DynoTrigger *;
}

# Keep reflection access for runtime
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable