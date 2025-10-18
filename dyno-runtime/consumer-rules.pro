# Consumer rules for dyno-runtime
-keep @interface com.ardayucesan.dyno.annotations.** { *; }
-keepclassmembers class * {
    @com.ardayucesan.dyno.annotations.DynoExpose *;
    @com.ardayucesan.dyno.annotations.DynoTrigger *;
}