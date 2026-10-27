package com.m57.hermescontrol

import androidx.navigation3.runtime.NavKey

/**
 * Fourche noronuke/hermes-mobile : l'appli sert à converser avec Hermes, pas à l'administrer.
 *
 * [dansLeMenu] : les seules entrées du tiroir de navigation, dans cet ordre.
 * [interdits] : écrans qui touchent aux secrets, à la configuration ou au serveur ; refusés
 * depuis tous les points d'entrée (tiroir, liens internes, notifications) par [ProfileLock.allows].
 * Le mot de passe du tableau de bord garde ses droits côté serveur : ceci n'est qu'un garde-fou local.
 */
object Allegement {
    val dansLeMenu: List<NavKey> = listOf(ChatScreen, HistoryScreen, ProjectsScreen, ModelScreen, SettingsScreen)

    val interdits: Set<NavKey> =
        setOf(
            ConfigScreen, KeysScreen, ProvidersScreen, PluginsScreen, GatewayScreen, SystemScreen,
            LogsScreen, FilesScreen, ChannelsScreen, PairingScreen, LocalModelsScreen, ConnectorsScreen,
            WebhooksScreen, BotsScreen,
        )

    /** Réglages cosmétiques de la conversation (statistiques, effet de frappe) : masqués. */
    val reglagesCosmetiques: Boolean = false

    /** Fourche noronuke : puces de filtre de la liste des conversations (section, archivées, source, projet). */
    val filtresConversations: Boolean = false

    /**
     * Fourche : changer de bot (profil Hermes) depuis l'appli reste LOCAL. Les requêtes portent
     * `profile` (voir WsProfileParams / ProfileScopeInterceptor) ; le profil par défaut du serveur
     * (`hermes profile use`, POST /api/profiles/active) n'est jamais modifié : des scripts et des
     * passerelles du serveur s'appuient sur ce défaut.
     */
    @Volatile
    var profilCollantServeur: Boolean = false // var : les tests de l'amont le remettent à true

    /** Fourche : sélecteur de bot (profil Hermes) dans l'en-tête du tiroir. */
    val selecteurBots: Boolean = true

    fun permet(key: NavKey): Boolean = key !in interdits
}
