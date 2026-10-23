package com.m57.hermescontrol.data.session

import com.m57.hermescontrol.data.model.SessionRenameRequest
import com.m57.hermescontrol.data.remote.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Fourche noronuke : lecture d'une conversation, pour la pastille verte « fini, pas encore lu ».
 *
 * Ouvrir puis quitter une conversation l'inscrit comme lue jusqu'à maintenant dans la base partagée
 * (`last_read_at`, le même filigrane que l'appli de bureau) et localement, pour que la liste se mette
 * à jour sans attendre le prochain chargement.
 */
object SessionReadMarker {
    /**
     * Avant cette date (mise en service de la fourche 2.7.3-noro.6), aucune surface n'écrivait le
     * filigrane : une conversation sans filigrane et sans activité depuis compte comme lue.
     */
    const val DEPUIS_S: Double = 1792766976.0

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val luesLocalement = ConcurrentHashMap<String, Double>()

    fun lueLocalementA(sessionId: String): Double? = luesLocalement[sessionId]

    fun marquerLue(sessionId: String?) {
        val id = sessionId?.takeIf { it.isNotBlank() } ?: return
        luesLocalement[id] = System.currentTimeMillis() / 1000.0
        scope.launch {
            runCatching { ApiClient.hermesApi.setSessionRead(id, SessionRenameRequest(unread = false)) }
        }
    }
}
