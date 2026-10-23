package com.m57.hermescontrol.theme.presets

import androidx.compose.ui.graphics.Color
import com.m57.hermescontrol.theme.HermesStatusColors
import com.m57.hermescontrol.theme.PaletteColors
import com.m57.hermescontrol.theme.buildTheme

// ---------------------------------------------------------------------
// Fourche noronuke : palette « Argile », dans l'esprit de l'appli Claude
// (fond crème ou brun chaud, accent terre cuite). Contrastes mesurés :
// texte >= 13:1, accent sur fond >= 5,1:1 en clair et 5,8:1 en sombre.
// Les noms Slate* sont gardés pour limiter l'écart avec l'amont.
// ---------------------------------------------------------------------

private val SlateDarkBg0 = Color(0xFF262624)
private val SlateDarkBg1 = Color(0xFF2B2B29)
private val SlateDarkBg2 = Color(0xFF30302E)
private val SlateDarkBg3 = Color(0xFF3A3A37)
private val SlateDarkBg4 = Color(0xFF454540)
private val SlateDarkFg1 = Color(0xFFF5F4EE)
private val SlateDarkFg2 = Color(0xFFECEAE0)
private val SlateDarkFg3 = Color(0xFFC2C0B6)
private val SlateGray = Color(0xFF9A988F)

private val SlateBrightTeal = Color(0xFFE08B6A) // terre cuite claire (accent sombre)
private val SlateBrightBlue = Color(0xFFD9A98C)
private val SlateBrightCyan = Color(0xFFD6CFC0)
private val SlateBrightGreen = Color(0xFF8FCB8F)
private val SlateBrightYellow = Color(0xFFE3C065)
private val SlateBrightRed = Color(0xFFF2A49B)
private val SlateBrightInfo = Color(0xFF9CC4EA)

private val SlateLightBg0 = Color(0xFFFAF9F5)
private val SlateLightBg1 = Color(0xFFF5F4ED)
private val SlateLightBg2 = Color(0xFFF0EEE6)
private val SlateLightBg3 = Color(0xFFE8E6DC)
private val SlateLightBg4 = Color(0xFFDEDCD1)
private val SlateLightFg1 = Color(0xFF141413)
private val SlateLightFg2 = Color(0xFF29261B)
private val SlateLightFg3 = Color(0xFF5E5D59)

private val SlateFadedTeal = Color(0xFFA9502C) // terre cuite (accent clair)
private val SlateFadedBlue = Color(0xFF8A4F33)
private val SlateFadedCyan = Color(0xFF6B5E4E)
private val SlateFadedGreen = Color(0xFF2E6B31)
private val SlateFadedYellow = Color(0xFF7A5A00)
private val SlateFadedRed = Color(0xFFB3261E)
private val SlateFadedInfo = Color(0xFF2F5F8A)

/** Thème par défaut de la fourche : « Argile », crème et terre cuite. */
val DefaultTheme =
    buildTheme(
        dark =
            PaletteColors(
                primary = SlateBrightTeal,
                onPrimary = SlateDarkBg0,
                primaryContainer = SlateDarkBg4,
                onPrimaryContainer = SlateDarkFg1,
                secondary = SlateBrightCyan,
                onSecondary = SlateDarkBg0,
                secondaryContainer = SlateDarkBg3,
                onSecondaryContainer = SlateDarkFg2,
                tertiary = SlateBrightBlue,
                onTertiary = SlateDarkBg0,
                tertiaryContainer = SlateDarkBg4,
                onTertiaryContainer = SlateDarkFg1,
                background = SlateDarkBg0,
                onBackground = SlateDarkFg1,
                surface = SlateDarkBg0,
                onSurface = SlateDarkFg1,
                surfaceVariant = SlateDarkBg1,
                onSurfaceVariant = SlateDarkFg3,
                surfaceDim = SlateDarkBg0,
                surfaceBright = SlateDarkBg4,
                primaryFixed = SlateBrightTeal,
                primaryFixedDim = SlateBrightTeal,
                onPrimaryFixed = SlateDarkBg0,
                onPrimaryFixedVariant = SlateDarkBg0,
                secondaryFixed = SlateBrightCyan,
                secondaryFixedDim = SlateBrightCyan,
                onSecondaryFixed = SlateDarkBg0,
                onSecondaryFixedVariant = SlateDarkBg0,
                tertiaryFixed = SlateBrightBlue,
                tertiaryFixedDim = SlateBrightBlue,
                onTertiaryFixed = SlateDarkBg0,
                onTertiaryFixedVariant = SlateDarkBg0,
                surfaceContainerLowest = SlateDarkBg0,
                surfaceContainerLow = SlateDarkBg1,
                surfaceContainer = SlateDarkBg2,
                surfaceContainerHigh = SlateDarkBg3,
                surfaceContainerHighest = SlateDarkBg4,
                inverseSurface = SlateDarkFg1,
                inverseOnSurface = SlateDarkBg0,
                inversePrimary = SlateFadedTeal,
                outline = SlateGray,
                outlineVariant = SlateDarkBg3,
                scrim = SlateDarkBg0,
                status =
                    HermesStatusColors(
                        success = SlateBrightGreen,
                        successContainer = SlateDarkBg3,
                        onSuccess = SlateDarkBg0,
                        warning = SlateBrightYellow,
                        warningContainer = SlateDarkBg3,
                        onWarning = SlateDarkBg0,
                        error = SlateBrightRed,
                        errorContainer = SlateDarkBg3,
                        onError = SlateDarkBg0,
                        onErrorContainer = SlateBrightRed,
                        info = SlateBrightInfo,
                        infoContainer = SlateDarkBg3,
                        onInfo = SlateDarkBg0,
                    ),
            ),
        light =
            PaletteColors(
                primary = SlateFadedTeal,
                onPrimary = SlateLightBg0,
                primaryContainer = SlateLightBg4,
                onPrimaryContainer = SlateLightFg1,
                secondary = SlateFadedCyan,
                onSecondary = SlateLightBg0,
                secondaryContainer = SlateLightBg3,
                onSecondaryContainer = SlateLightFg2,
                tertiary = SlateFadedBlue,
                onTertiary = SlateLightBg0,
                tertiaryContainer = SlateLightBg4,
                onTertiaryContainer = SlateLightFg1,
                background = SlateLightBg0,
                onBackground = SlateLightFg1,
                surface = SlateLightBg0,
                onSurface = SlateLightFg1,
                surfaceVariant = SlateLightBg1,
                onSurfaceVariant = SlateLightFg3,
                surfaceDim = SlateLightBg4,
                surfaceBright = SlateLightBg0,
                primaryFixed = SlateBrightTeal,
                primaryFixedDim = SlateBrightTeal,
                onPrimaryFixed = SlateDarkBg0,
                onPrimaryFixedVariant = SlateDarkBg0,
                secondaryFixed = SlateBrightCyan,
                secondaryFixedDim = SlateBrightCyan,
                onSecondaryFixed = SlateDarkBg0,
                onSecondaryFixedVariant = SlateDarkBg0,
                tertiaryFixed = SlateBrightBlue,
                tertiaryFixedDim = SlateBrightBlue,
                onTertiaryFixed = SlateDarkBg0,
                onTertiaryFixedVariant = SlateDarkBg0,
                surfaceContainerLowest = SlateLightBg0,
                surfaceContainerLow = SlateLightBg1,
                surfaceContainer = SlateLightBg2,
                surfaceContainerHigh = SlateLightBg3,
                surfaceContainerHighest = SlateLightBg4,
                inverseSurface = SlateLightFg1,
                inverseOnSurface = SlateLightBg0,
                inversePrimary = SlateBrightTeal,
                outline = SlateLightFg3,
                outlineVariant = SlateLightBg3,
                scrim = SlateLightBg0,
                status =
                    HermesStatusColors(
                        success = SlateFadedGreen,
                        successContainer = SlateLightBg3,
                        onSuccess = SlateLightBg0,
                        warning = SlateFadedYellow,
                        warningContainer = SlateLightBg3,
                        onWarning = SlateLightBg0,
                        error = SlateFadedRed,
                        errorContainer = SlateLightBg3,
                        onError = SlateLightBg0,
                        onErrorContainer = SlateFadedRed,
                        info = SlateFadedInfo,
                        infoContainer = SlateLightBg3,
                        onInfo = SlateLightBg0,
                    ),
            ),
    )
