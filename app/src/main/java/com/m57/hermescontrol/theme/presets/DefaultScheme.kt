package com.m57.hermescontrol.theme.presets

import androidx.compose.ui.graphics.Color
import com.m57.hermescontrol.theme.HermesStatusColors
import com.m57.hermescontrol.theme.PaletteColors
import com.m57.hermescontrol.theme.buildTheme

// ---------------------------------------------------------------------
// Fourche noronuke : palette « Maquis » (charte commune des applis, 26/10/2026),
// accent bordeaux propre à Hermes. Fond crème, cartes papier, encre, filets.
// Contrastes calculés (WCAG) : encre/fond 12,9:1 en clair, 14,3:1 en sombre ;
// accent/fond 7,9:1 en clair, 8,4:1 en sombre ; toutes les paires des tests >= 4,5:1.
// ---------------------------------------------------------------------

// Clair
private val Creme = Color(0xFFF2EFE6)
private val Papier = Color(0xFFFAF8F2)
private val PapierBas = Color(0xFFF6F3EC)
private val Lin = Color(0xFFEEEAE0)
private val LinHaut = Color(0xFFE8E4D8)
private val Filet = Color(0xFFDFDCCF)
private val CremeVariante = Color(0xFFECE8DD)
private val CremeTerne = Color(0xFFE6E2D6)
private val Encre = Color(0xFF1F2A24)
private val EncreDouce = Color(0xFF4A554D)
private val Sauge = Color(0xFF6E766D)
private val Bordeaux = Color(0xFF7D2E3A)
private val BordeauxFonce = Color(0xFF6B2432)
private val RosePale = Color(0xFFEFE3E0)
private val Olive = Color(0xFF5E6B47)
private val SaugePale = Color(0xFFE5E9DC)
private val BleuNuit = Color(0xFF1F3147)
private val OcrePale = Color(0xFFEFE8D4)
private val VertClair = Color(0xFF4C6631)
private val OcreClair = Color(0xFF7A5A00)
private val RougeClair = Color(0xFFA3262A)
private val RougeClairTexte = Color(0xFF8E1F25)
private val BleuClair = Color(0xFF2E5884)

// Sombre
private val Nuit = Color(0xFF151B17)
private val NuitBasse = Color(0xFF111612)
private val NuitBas = Color(0xFF1A211D)
private val NuitSurface = Color(0xFF1E2621)
private val NuitHaut = Color(0xFF252E28)
private val NuitPlusHaut = Color(0xFF2E3831)
private val NuitClaire = Color(0xFF333E36)
private val CremeTexte = Color(0xFFECE8DC)
private val CremeDouce = Color(0xFFB8B9AC)
private val SaugeSombre = Color(0xFF8A9187)
private val BordeauxEclairci = Color(0xFFE7A1AB)
private val BordeauxNuit = Color(0xFF2A0E14)
private val BordeauxProfond = Color(0xFF5A2430)
private val RoseTexte = Color(0xFFF6E4E7)
private val OliveEclaircie = Color(0xFFB5C391)
private val OliveProfonde = Color(0xFF2F3A2A)
private val BleuNuitEclairci = Color(0xFFA9BCD6)
private val OcreProfonde = Color(0xFF3A3322)
private val VertSombre = Color(0xFF9DBE7A)
private val OcreSombre = Color(0xFFD9B45A)
private val RougeSombre = Color(0xFFF0A0A0)
private val RougeProfond = Color(0xFF3A2125)
private val BleuSombre = Color(0xFF8FB4DE)

// Rôles « fixes » : identiques dans les deux modes (exigé par ThemePaletteTest).
private val FixeBordeaux = RosePale
private val FixeBordeauxTerne = Color(0xFFE3CDC9)
private val SurFixeBordeaux = Color(0xFF3A1219)
private val FixeSauge = SaugePale
private val FixeSaugeTerne = Color(0xFFD3DAC4)
private val SurFixeSaugeVariante = Color(0xFF3F4A33)
private val FixeOcre = OcrePale
private val FixeOcreTerne = Color(0xFFE2D6B4)

/** Thème par défaut de la fourche : « Maquis », crème, encre et bordeaux. */
val DefaultTheme =
    buildTheme(
        dark =
            PaletteColors(
                primary = BordeauxEclairci,
                onPrimary = BordeauxNuit,
                primaryContainer = BordeauxProfond,
                onPrimaryContainer = RoseTexte,
                secondary = OliveEclaircie,
                onSecondary = Nuit,
                secondaryContainer = OliveProfonde,
                onSecondaryContainer = SaugePale,
                tertiary = BleuNuitEclairci,
                onTertiary = Nuit,
                tertiaryContainer = OcreProfonde,
                onTertiaryContainer = OcrePale,
                background = Nuit,
                onBackground = CremeTexte,
                surface = Nuit,
                onSurface = CremeTexte,
                surfaceVariant = NuitSurface,
                onSurfaceVariant = CremeDouce,
                surfaceDim = NuitBasse,
                surfaceBright = NuitClaire,
                primaryFixed = FixeBordeaux,
                primaryFixedDim = FixeBordeauxTerne,
                onPrimaryFixed = SurFixeBordeaux,
                onPrimaryFixedVariant = BordeauxFonce,
                secondaryFixed = FixeSauge,
                secondaryFixedDim = FixeSaugeTerne,
                onSecondaryFixed = Encre,
                onSecondaryFixedVariant = SurFixeSaugeVariante,
                tertiaryFixed = FixeOcre,
                tertiaryFixedDim = FixeOcreTerne,
                onTertiaryFixed = Encre,
                onTertiaryFixedVariant = BleuNuit,
                surfaceContainerLowest = NuitBasse,
                surfaceContainerLow = NuitBas,
                surfaceContainer = NuitSurface,
                surfaceContainerHigh = NuitHaut,
                surfaceContainerHighest = NuitPlusHaut,
                inverseSurface = CremeTexte,
                inverseOnSurface = Nuit,
                inversePrimary = Bordeaux,
                outline = SaugeSombre,
                outlineVariant = NuitPlusHaut,
                scrim = Color.Black,
                status =
                    HermesStatusColors(
                        success = VertSombre,
                        successContainer = OliveProfonde,
                        onSuccess = Nuit,
                        warning = OcreSombre,
                        warningContainer = OcreProfonde,
                        onWarning = Nuit,
                        error = RougeSombre,
                        errorContainer = RougeProfond,
                        onError = Nuit,
                        onErrorContainer = RougeSombre,
                        info = BleuSombre,
                        infoContainer = NuitPlusHaut,
                        onInfo = Nuit,
                        neutral = SaugeSombre,
                    ),
            ),
        light =
            PaletteColors(
                primary = Bordeaux,
                onPrimary = Papier,
                primaryContainer = RosePale,
                onPrimaryContainer = BordeauxFonce,
                secondary = Olive,
                onSecondary = Papier,
                secondaryContainer = SaugePale,
                onSecondaryContainer = Encre,
                tertiary = BleuNuit,
                onTertiary = Papier,
                tertiaryContainer = OcrePale,
                onTertiaryContainer = Encre,
                background = Creme,
                onBackground = Encre,
                surface = Creme,
                onSurface = Encre,
                surfaceVariant = CremeVariante,
                onSurfaceVariant = EncreDouce,
                surfaceDim = CremeTerne,
                surfaceBright = Papier,
                primaryFixed = FixeBordeaux,
                primaryFixedDim = FixeBordeauxTerne,
                onPrimaryFixed = SurFixeBordeaux,
                onPrimaryFixedVariant = BordeauxFonce,
                secondaryFixed = FixeSauge,
                secondaryFixedDim = FixeSaugeTerne,
                onSecondaryFixed = Encre,
                onSecondaryFixedVariant = SurFixeSaugeVariante,
                tertiaryFixed = FixeOcre,
                tertiaryFixedDim = FixeOcreTerne,
                onTertiaryFixed = Encre,
                onTertiaryFixedVariant = BleuNuit,
                surfaceContainerLowest = Papier,
                surfaceContainerLow = PapierBas,
                surfaceContainer = Lin,
                surfaceContainerHigh = LinHaut,
                surfaceContainerHighest = Filet,
                inverseSurface = Encre,
                inverseOnSurface = Creme,
                inversePrimary = BordeauxEclairci,
                outline = Sauge,
                outlineVariant = Filet,
                scrim = Encre,
                status =
                    HermesStatusColors(
                        success = VertClair,
                        successContainer = SaugePale,
                        onSuccess = Papier,
                        warning = OcreClair,
                        warningContainer = OcrePale,
                        onWarning = Papier,
                        error = RougeClair,
                        errorContainer = RosePale,
                        onError = Papier,
                        onErrorContainer = RougeClairTexte,
                        info = BleuClair,
                        infoContainer = Color(0xFFE1E7EE),
                        onInfo = Papier,
                        neutral = Sauge,
                    ),
            ),
    )
