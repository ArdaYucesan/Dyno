package com.ardayucesan.dyno.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Professional Dyno Color Palette
private val DynoBlue = Color(0xFF1E88E5)          // Primary blue - professional and trustworthy
private val DynoDeepBlue = Color(0xFF0D47A1)      // Darker blue for contrast
private val DynoAccent = Color(0xFF26C6DA)        // Cyan accent - modern and tech-forward
private val DynoSuccess = Color(0xFF43A047)       // Green for success states
private val DynoWarning = Color(0xFFFF9800)       // Orange for warnings
private val DynoError = Color(0xFFE53935)         // Red for errors
private val DynoNeutral = Color(0xFF607D8B)       // Blue-grey for secondary elements

private val DarkColorScheme = darkColorScheme(
    primary = DynoBlue,
    onPrimary = Color.White,
    primaryContainer = DynoDeepBlue,
    onPrimaryContainer = Color.White,
    secondary = DynoAccent,
    onSecondary = Color.Black,
    secondaryContainer = DynoAccent.copy(alpha = 0.2f),
    onSecondaryContainer = Color.White,
    tertiary = DynoSuccess,
    onTertiary = Color.White,
    background = Color(0xFF0F1419),               // Very dark blue-grey
    onBackground = Color(0xFFE1E8ED),
    surface = Color(0xFF1A1F26),                  // Dark surface with slight blue tint
    onSurface = Color(0xFFE1E8ED),
    surfaceVariant = Color(0xFF2A3038),           // Slightly lighter surface
    onSurfaceVariant = Color(0xFFB8C5D1),
    outline = DynoNeutral.copy(alpha = 0.5f),
    error = DynoError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = DynoBlue,
    onPrimary = Color.White,
    primaryContainer = DynoBlue.copy(alpha = 0.1f),
    onPrimaryContainer = DynoDeepBlue,
    secondary = DynoAccent,
    onSecondary = Color.White,
    secondaryContainer = DynoAccent.copy(alpha = 0.1f),
    onSecondaryContainer = DynoDeepBlue,
    tertiary = DynoSuccess,
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),               // Very light blue-grey
    onBackground = Color(0xFF1A202C),
    surface = Color.White,
    onSurface = Color(0xFF1A202C),
    surfaceVariant = Color(0xFFF1F5F9),           // Light grey-blue
    onSurfaceVariant = Color(0xFF475569),
    outline = DynoNeutral.copy(alpha = 0.3f),
    error = DynoError,
    onError = Color.White
)

// Professional Typography
private val DynoTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
)

@Composable
fun DynoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DynoTypography,
        content = content
    )
}