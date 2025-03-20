package com.example.composeapptask.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomColorScheme(
    val customPrimary: Color,
    val customSecondary: Color,
    val whiteColor: Color,
    val customError: Color = Color(0xFFB00020),
    val baseColorScheme: ColorScheme,
    val grayColor: Color,
    val greenColor: Color,
    val successGreen: Color,
    val accentPurple: Color,
    val mediumGray: Color,
    val white: Color,
    val black: Color,
)

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary
)

fun lightCustomColors(primaryColor: Color?) = CustomColorScheme(
    baseColorScheme = LightColors,
    customPrimary = primaryColor ?: md_theme_light_secondary,
    customSecondary = Color(0xFF03DAC6),
    whiteColor = Color(0xFFFFFFFF),
    grayColor = Color(0xff858A93),
    greenColor = Color(0xff4BDF97),
    successGreen = md_theme_light_SuccessGreen,
    accentPurple = md_theme_light_AccentPurple,
    mediumGray = md_theme_light_MediumGray,
    white = md_theme_light_White,
    black = md_theme_light_scrim
)

fun darkCustomColors(primaryColor: Color?) = CustomColorScheme(
    baseColorScheme = DarkColors,
    customPrimary = primaryColor ?: md_theme_light_secondary,
    customSecondary = Color(0xFF03DAC6),
    whiteColor = Color(0xFF121212),
    grayColor = Color(0xff858A93),
    greenColor = Color(0xff4BDF97),
    successGreen = md_theme_dark_SuccessGreen,
    accentPurple = md_theme_dark_AccentPurple,
    mediumGray = md_theme_dark_MediumGray,
    white = md_theme_dark_White,
    black = md_theme_light_scrim
)

private val LocalAppDimens = staticCompositionLocalOf { normalDimensions }
val LocalCustomColors = staticCompositionLocalOf { lightCustomColors(primaryColor = md_theme_light_secondary) }

@SuppressLint("CompositionLocalNaming")
internal val currentTypography = staticCompositionLocalOf { AppTypography() }

@Composable
fun ProvideDimens(
    dimensions: Dimensions,
    content: @Composable () -> Unit
) {
    val dimensionSet = remember { dimensions }
    CompositionLocalProvider(LocalAppDimens provides dimensionSet, content = content)
}


@Composable
fun CustomAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
    primaryColor: Color?
) {
    val customColors = if (darkTheme) darkCustomColors(primaryColor) else lightCustomColors(primaryColor)
    val dimensions = normalDimensions
    val typography = AppTypography()
    CompositionLocalProvider(
        LocalCustomColors provides customColors
    ) {
        ProvideDimens(dimensions = dimensions) {
            MaterialTheme(
                colorScheme = customColors.baseColorScheme,
                content = content,
                typography = MaterialTheme.typography.copy(
                    displayLarge = typography.displayLarge,
                    displayMedium = typography.displayMedium,
                    displaySmall = typography.displaySmall,
                    headlineLarge = typography.headlineLarge,
                    headlineMedium = typography.headlineMedium,
                    headlineSmall = typography.headlineSmall,
                    titleLarge = typography.titleLarge,
                    titleMedium = typography.titleMedium,
                    titleSmall = typography.titleSmall,
                    bodyLarge = typography.bodyLarge,
                    bodyMedium = typography.bodyMedium,
                    bodySmall = typography.bodySmall,
                    labelLarge = typography.labelLarge,
                    labelMedium = typography.labelMedium,
                    labelSmall = typography.labelSmall
                ),
            )
        }

    }
}

object AppTheme {
    val dimens: Dimensions
        @Composable
        get() = LocalAppDimens.current

    val colors: CustomColorScheme
        @Composable
        get() = LocalCustomColors.current

    val typography: AppTypography
        @Composable
        @ReadOnlyComposable
        get() = currentTypography.current
}