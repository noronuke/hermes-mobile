package com.m57.hermescontrol.data.model

import kotlinx.serialization.Serializable

/**
 * Fourche noronuke : réponse du greffon serveur `arriere-plan` (~/.hermes/plugins/arriere-plan).
 * [sessions] liste les conversations qui ont au moins un processus en arrière-plan vivant,
 * quelle que soit la surface qui l'a lancé (bureau, Signal, mobile), descendantes de compression comprises.
 */
@Serializable
data class ArrierePlanResponse(
    val sessions: List<String> = emptyList(),
)
