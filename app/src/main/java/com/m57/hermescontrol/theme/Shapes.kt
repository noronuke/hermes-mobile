package com.m57.hermescontrol.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Fourche noronuke : un seul rayon par famille (charte commune des applis, 26/10/2026).
 *
 * | Famille   | Rayon   | Usage                                                   |
 * |-----------|---------|---------------------------------------------------------|
 * | Fin       | 6 dp    | petits éléments en ligne (code, barres, vignettes)      |
 * | Contrôle  | 12 dp   | champs, boutons carrés, menus, blocs dans une carte     |
 * | Carte     | 16 dp   | cartes, encadrés, tiroir                                |
 * | Bulle     | 20 dp   | bulles de message                                       |
 * | Feuille   | 28 dp   | feuilles, dialogues, barre de saisie flottante          |
 * | Puce      | rond    | puces, pastilles, boutons-icônes                        |
 *
 * Règle : jamais un élément arrondi posé dans un carré. Un élément intérieur prend la famille
 * du dessous (ou une puce ronde), jamais un rayon plus grand que son contenant.
 */
object Formes {
    val Fin = RoundedCornerShape(6.dp)
    val Controle = RoundedCornerShape(12.dp)
    val Carte = RoundedCornerShape(16.dp)
    val Bulle = RoundedCornerShape(20.dp)

    /** Bulle envoyée : coin de départ en bas à droite, comme les messageries courantes. */
    val BulleEnvoyee = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 6.dp, bottomStart = 20.dp)
    val Feuille = RoundedCornerShape(28.dp)
    val Puce = RoundedCornerShape(50)
}

val HermesShapes =
    Shapes(
        // Champs de saisie, menus, infobulles.
        extraSmall = Formes.Controle,
        // Puces (AssistChip, FilterChip…) : pleines rondes.
        small = Formes.Puce,
        // Cartes.
        medium = Formes.Carte,
        // Tiroir, grandes cartes, bouton flottant.
        large = Formes.Carte,
        // Dialogues et feuilles.
        extraLarge = Formes.Feuille,
    )
