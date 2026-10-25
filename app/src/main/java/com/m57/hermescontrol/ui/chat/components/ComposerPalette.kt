package com.m57.hermescontrol.ui.chat.components

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import com.m57.hermescontrol.theme.GlyphWhite

/**
 * Resolved colors for the chat composer card and its controls.
 *
 * Control fills are derived from [ColorScheme.onSurface] instead of the
 * surfaceContainer tiers: several presets ship nearly identical tiers
 * (Nord's High and Highest are the same color), which made the flat controls
 * vanish into the card. A fixed onSurface tint keeps them visible in every
 * preset and under dynamic color. Text and icon contrast is gated per preset
 * in ComposerPaletteTest. The send / stop / mic action button is the one
 * themed control: it takes the preset's primary so it follows the theme tint.
 */
internal data class ComposerPalette(
    val card: Color,
    val cardBorder: Color,
    val text: Color,
    val placeholder: Color,
    val control: Color,
    val onControl: Color,
    val onControlVariant: Color,
    val action: Color,
    val onAction: Color,
)

private const val CONTROL_TINT_ALPHA = 0.12f
private const val ACTION_MIN_CONTRAST = 3f

private fun contrastRatio(
    a: Color,
    b: Color,
): Float {
    val l1 = a.luminance()
    val l2 = b.luminance()
    return (maxOf(l1, l2) + 0.05f) / (minOf(l1, l2) + 0.05f)
}

internal fun composerPalette(scheme: ColorScheme): ComposerPalette {
    val tint = scheme.onSurface.copy(alpha = CONTROL_TINT_ALPHA)
    // Fourche noronuke : barre de saisie « papier » qui flotte sur le fond (papier en clair, palier
    // haut en sombre), bordée d'un filet. Repli sur le palier habituel si un contraste n'y tient pas.
    val light = scheme.background.luminance() > 0.5f
    val paperCard = if (light) scheme.surfaceContainerLowest else scheme.surfaceContainerHigh
    val paperOk =
        contrastRatio(scheme.onSurface, paperCard) >= 4.5f &&
            contrastRatio(scheme.onSurfaceVariant, paperCard) >= 3f &&
            contrastRatio(scheme.onSurfaceVariant, tint.compositeOver(paperCard)) >= 3f &&
            contrastRatio(scheme.onSurface, tint.compositeOver(paperCard)) >= 4.5f &&
            contrastRatio(tint.compositeOver(paperCard), paperCard) >= 1.15f
    val card = if (paperOk) paperCard else scheme.surfaceContainer
    val hairline =
        if (light) scheme.outlineVariant else scheme.onSurface.copy(alpha = 0.16f).compositeOver(scheme.background)
    val border =
        if (contrastRatio(hairline, scheme.background) >= 1.15f) hairline else tint.compositeOver(scheme.background)
    // Pastel primaries (Nord light) vanish on the card; keep the neutral fill there.
    val tinted = contrastRatio(scheme.primary, card) >= ACTION_MIN_CONTRAST
    val action = if (tinted) scheme.primary else scheme.onSurface
    // White glyph wherever it clears 3:1 on the primary; pastel primaries keep their own dark onPrimary.
    val onAction =
        when {
            !tinted -> scheme.surface
            contrastRatio(GlyphWhite, scheme.primary) >= ACTION_MIN_CONTRAST -> GlyphWhite
            else -> scheme.onPrimary
        }
    return ComposerPalette(
        card = card,
        cardBorder = border,
        text = scheme.onSurface,
        placeholder = scheme.onSurfaceVariant,
        control = tint.compositeOver(card),
        onControl = scheme.onSurface,
        onControlVariant = scheme.onSurfaceVariant,
        action = action,
        onAction = onAction,
    )
}

@Composable
@ReadOnlyComposable
internal fun composerPalette(): ComposerPalette = composerPalette(MaterialTheme.colorScheme)
