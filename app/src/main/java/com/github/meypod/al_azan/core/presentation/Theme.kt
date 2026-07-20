package com.github.meypod.al_azan.core.presentation

import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowCompat
import com.github.meypod.al_azan.core.domain.model.settings.ThemeColor

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    scrim = DarkScrim,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    inversePrimary = DarkInversePrimary,
    surfaceTint = DarkSurfaceTint,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
)
// AMOLED variant: pure black background and surfaces, copy colors otherwise identical to the
// standard dark scheme so accents/tertiary/error retain proper M3 contrast against true black.
private val AmoledColorScheme = DarkColorScheme.copy(
    background = Color(0xFF000000),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF000000),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFFBDBDBD),
    surfaceTint = DarkPrimary,
    surfaceContainerLowest = Color(0xFF000000),
    surfaceContainerLow = Color(0xFF080808),
    surfaceContainer = Color(0xFF0F0F0F),
    surfaceContainerHigh = Color(0xFF161616),
    surfaceContainerHighest = Color(0xFF1D1D1D),
    inverseSurface = Color(0xFFE6E6E6),
    inverseOnSurface = Color(0xFF1A1A1A),
    scrim = Color(0xFF000000),
)
val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    scrim = LightScrim,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    inversePrimary = LightInversePrimary,
    surfaceTint = LightSurfaceTint,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerLowest = LightSurfaceContainerLowest,
    surfaceContainerHighest = LightSurfaceContainerHighest,
)

private val LightHighContrastColorScheme = lightColorScheme(
    primary = LightHighContrastPrimary,
    onPrimary = LightHighContrastOnPrimary,
    primaryContainer = LightHighContrastPrimaryContainer,
    onPrimaryContainer = LightHighContrastOnPrimaryContainer,
    secondary = LightHighContrastSecondary,
    onSecondary = LightHighContrastOnSecondary,
    secondaryContainer = LightHighContrastSecondaryContainer,
    onSecondaryContainer = LightHighContrastOnSecondaryContainer,
    tertiary = LightHighContrastTertiary,
    onTertiary = LightHighContrastOnTertiary,
    tertiaryContainer = LightHighContrastTertiaryContainer,
    onTertiaryContainer = LightHighContrastOnTertiaryContainer,
    error = LightHighContrastError,
    onError = LightHighContrastOnError,
    errorContainer = LightHighContrastErrorContainer,
    onErrorContainer = LightHighContrastOnErrorContainer,
    background = LightHighContrastBackground,
    onBackground = LightHighContrastOnBackground,
    // spec surface equals background here; use the container tone so elevated
    // surfaces (cards) stay distinct from the page background
    surface = LightHighContrastSurfaceContainer,
    onSurface = LightHighContrastOnSurface,
    surfaceVariant = LightHighContrastSurfaceVariant,
    onSurfaceVariant = LightHighContrastOnSurfaceVariant,
    outline = LightHighContrastOutline,
    outlineVariant = LightHighContrastOutlineVariant,
    scrim = LightHighContrastScrim,
    inverseSurface = LightHighContrastInverseSurface,
    inverseOnSurface = LightHighContrastInverseOnSurface,
    inversePrimary = LightHighContrastInversePrimary,
    surfaceTint = LightHighContrastSurfaceTint,
    surfaceContainerLowest = LightHighContrastSurfaceContainerLowest,
    surfaceContainerLow = LightHighContrastSurfaceContainerLow,
    surfaceContainer = LightHighContrastSurfaceContainer,
    surfaceContainerHigh = LightHighContrastSurfaceContainerHigh,
    surfaceContainerHighest = LightHighContrastSurfaceContainerHighest,
)

private val DarkHighContrastColorScheme = darkColorScheme(
    primary = DarkHighContrastPrimary,
    onPrimary = DarkHighContrastOnPrimary,
    primaryContainer = DarkHighContrastPrimaryContainer,
    onPrimaryContainer = DarkHighContrastOnPrimaryContainer,
    secondary = DarkHighContrastSecondary,
    onSecondary = DarkHighContrastOnSecondary,
    secondaryContainer = DarkHighContrastSecondaryContainer,
    onSecondaryContainer = DarkHighContrastOnSecondaryContainer,
    tertiary = DarkHighContrastTertiary,
    onTertiary = DarkHighContrastOnTertiary,
    tertiaryContainer = DarkHighContrastTertiaryContainer,
    onTertiaryContainer = DarkHighContrastOnTertiaryContainer,
    error = DarkHighContrastError,
    onError = DarkHighContrastOnError,
    errorContainer = DarkHighContrastErrorContainer,
    onErrorContainer = DarkHighContrastOnErrorContainer,
    background = DarkHighContrastBackground,
    onBackground = DarkHighContrastOnBackground,
    // spec surface equals background here; use the container tone so elevated
    // surfaces (cards) stay distinct from the page background
    surface = DarkHighContrastSurfaceContainer,
    onSurface = DarkHighContrastOnSurface,
    surfaceVariant = DarkHighContrastSurfaceVariant,
    onSurfaceVariant = DarkHighContrastOnSurfaceVariant,
    outline = DarkHighContrastOutline,
    outlineVariant = DarkHighContrastOutlineVariant,
    scrim = DarkHighContrastScrim,
    inverseSurface = DarkHighContrastInverseSurface,
    inverseOnSurface = DarkHighContrastInverseOnSurface,
    inversePrimary = DarkHighContrastInversePrimary,
    surfaceTint = DarkHighContrastSurfaceTint,
    surfaceContainerLowest = DarkHighContrastSurfaceContainerLowest,
    surfaceContainerLow = DarkHighContrastSurfaceContainerLow,
    surfaceContainer = DarkHighContrastSurfaceContainer,
    surfaceContainerHigh = DarkHighContrastSurfaceContainerHigh,
    surfaceContainerHighest = DarkHighContrastSurfaceContainerHighest,
)

@Composable
fun AlAzanTheme(
    themeColor: ThemeColor = ThemeColor.Default,
    displayScale: Float = 1f,
    customSeedColor: Int? = null,
    content: @Composable () -> Unit,
) {
    val systemDarkTheme = isSystemInDarkTheme()
    val darkTheme = when (themeColor) {
        ThemeColor.Light, ThemeColor.ClassicLight -> false
        ThemeColor.Dark, ThemeColor.ClassicDark, ThemeColor.Amoled -> true
        ThemeColor.Dynamic, ThemeColor.Default -> systemDarkTheme
    }
    val colorScheme = when {
        // Dynamic with custom seed: OS dynamic scheme is overridden by the user-supplied seed on M3
        //佼 capable devices (Android 12+) by remapping primary/primaryContainer/secondary/tertiary及其
        // tonal derivatives. Pre-S falls through to the static Light/Dark schemes.
        themeColor == ThemeColor.Dynamic && customSeedColor != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            val base = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            base.withCustomSeed(customSeedColor, darkTheme)
        }

        themeColor == ThemeColor.Dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            val dynamic = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            // dynamic schemes set surface == background; shift surface to the container
            // tone so elevated surfaces (cards) stay distinct from the page background
            dynamic.copy(surface = dynamic.surfaceContainer)
        }

        themeColor == ThemeColor.Default && customSeedColor != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            val base = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            base.withCustomSeed(customSeedColor, darkTheme)
        }

        else ->
            when (themeColor) {
                ThemeColor.Light -> LightColorScheme

                ThemeColor.Dark -> DarkColorScheme

                ThemeColor.Amoled -> if (darkTheme) AmoledColorScheme else LightColorScheme

                ThemeColor.ClassicLight -> LightHighContrastColorScheme

                ThemeColor.ClassicDark -> DarkHighContrastColorScheme

                ThemeColor.Dynamic,
                ThemeColor.Default,
                -> if (darkTheme) DarkColorScheme else LightColorScheme
            }
    }

    val activity = LocalActivity.current
    if (activity != null) {
        // keep status/navigation bar icon contrast in sync with the resolved app theme,
        // which can differ from the system dark-mode setting
        SideEffect {
            val controller = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    val density = LocalDensity.current
    // Scale the dp density (not fontScale) so the whole UI — text, icons, spacing — zooms uniformly
    // by the slider. sp grows through density too, so text isn't scaled twice; the system font scale
    // stays in fontScale and still stacks on top.
    val scaledDensity = remember(density, displayScale) {
        Density(density.density * displayScale, density.fontScale)
    }

    // Persian/Arabic UI ship a bundled font whose metrics sit better with Latin than the system
    // Arabic fallback; other locales keep the default. Gated on the applied UI locale, not device.
    val typography = when (LocalConfiguration.current.locales[0].language) {
        "fa" -> VazirmatnTypography
        "ar" -> NotoSansArabicTypography
        else -> Typography
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
    ) {
        CompositionLocalProvider(LocalDensity provides scaledDensity, content = content)
    }
}

/**
 * Applies a user-supplied seed color to a Material 3 [ColorScheme] by blending the primary,
 * primaryContainer, secondary/tertiary counterparts toward the seed's tone. This is a pragmatic
 * approximation of the M3 HCT-based tonal palette (which is unavailable outside of
 * `dynamic*ColorScheme`); for the vast majority of seed colors it gives a pleasing, on-brand result
 * while keeping the rest of the dynamic scheme (background, surfaces, error) intact.
 */
private fun ColorScheme.withCustomSeed(seedArgb: Int, dark: Boolean): ColorScheme {
    val seed = Color(seedArgb)
    val targetPrimary = if (dark) seed.copy(alpha = 1f) else lerp(seed, Color.White, 0.05f)
    val targetPrimaryContainer = if (dark) lerp(seed, Color.Black, 0.4f) else lerp(seed, Color.White, 0.7f)
    val targetSecondary = lerp(seed, Color(0xFF6E7979), 0.4f)
    val targetTertiary = lerp(seed, Color(0xFFECC622), 0.5f)
    return copy(
        primary = targetPrimary,
        onPrimary = if (dark) Color(0xFF003738) else Color.White,
        primaryContainer = targetPrimaryContainer,
        onPrimaryContainer = if (dark) Color.White else Color(0xFF002728),
        secondary = targetSecondary,
        secondaryContainer = if (dark) lerp(targetSecondary, Color.Black, 0.4f) else lerp(targetSecondary, Color.White, 0.7f),
        tertiary = targetTertiary,
        surfaceTint = targetPrimary,
        inversePrimary = if (dark) lerp(seed, Color.White, 0.2f) else seed,
    )
}
