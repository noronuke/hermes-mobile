package com.m57.hermescontrol.ui.selecteurbot

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.m57.hermescontrol.R
import com.m57.hermescontrol.data.local.AuthManager
import com.m57.hermescontrol.data.model.ProfileInfo
import com.m57.hermescontrol.data.remote.ApiClient
import com.m57.hermescontrol.data.remote.NetworkResult
import com.m57.hermescontrol.data.remote.safeApiCall
import com.m57.hermescontrol.data.session.ProfileSwitchCoordinator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Fourche : choisir à quel bot (profil Hermes) on parle, depuis l'en-tête du tiroir.
 *
 * La liste vient de GET /api/profiles (lecture seule). Le choix passe par
 * [ProfileSwitchCoordinator.switchProfile] : sélection locale, prise publiée (les écrans
 * rechargent leurs données) et socket rouverte ; avec Allegement.profilCollantServeur à false,
 * le profil par défaut du serveur n'est jamais modifié. Aucune création, suppression ni
 * configuration de profil ici : c'est le rôle du tableau de bord, côté serveur.
 */
@Composable
fun SelecteurBot(onChoisi: () -> Unit) {
    val context = LocalContext.current
    val portee = rememberCoroutineScope()
    val actif by AuthManager.activeProfileId.collectAsState()
    var ouvert by remember { mutableStateOf(false) }
    var profils by remember { mutableStateOf<List<ProfileInfo>?>(null) }
    var chargement by remember { mutableStateOf(false) }
    var bascule by remember { mutableStateOf<String?>(null) }

    val nomActif = actif ?: AuthManager.DEFAULT_PROFILE_ID
    val principal = stringResource(R.string.bot_principal)
    fun libelle(nom: String, info: ProfileInfo?): String =
        info?.display_name?.takeIf { it.isNotBlank() }
            ?: if (nom == AuthManager.DEFAULT_PROFILE_ID) principal else nom.replaceFirstChar { it.uppercase() }

    LaunchedEffect(ouvert) {
        if (!ouvert || profils != null) return@LaunchedEffect
        chargement = true
        when (val r = withContext(Dispatchers.IO) { safeApiCall { ApiClient.hermesApi.getProfiles() } }) {
            is NetworkResult.Success -> profils = r.data.profiles
            is NetworkResult.Failure -> {
                ouvert = false
                Toast.makeText(
                    context,
                    context.getString(R.string.bot_selecteur_liste_echec, r.error.message),
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
        chargement = false
    }

    Box(modifier = Modifier.padding(horizontal = 12.dp)) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag("selecteur_bot")
                    .clickable(enabled = bascule == null) { ouvert = true },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Icon(Icons.Outlined.SmartToy, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.bot_selecteur_titre),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = libelle(nomActif, profils?.firstOrNull { it.name == nomActif }),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (bascule != null || chargement) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                }
            }
        }

        DropdownMenu(expanded = ouvert && profils != null, onDismissRequest = { ouvert = false }) {
            profils.orEmpty().forEach { p ->
                DropdownMenuItem(
                    modifier = Modifier.testTag("bot_${p.name}"),
                    leadingIcon = {
                        if (p.name == nomActif) Icon(Icons.Filled.Check, contentDescription = null) else Spacer(Modifier.size(24.dp))
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(libelle(p.name, p), style = MaterialTheme.typography.bodyLarge)
                            p.description?.takeIf { it.isNotBlank() }?.let {
                                Text(
                                    it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    },
                    onClick = {
                        ouvert = false
                        if (p.name == nomActif) {
                            onChoisi()
                            return@DropdownMenuItem
                        }
                        bascule = p.name
                        portee.launch {
                            when (val r = ProfileSwitchCoordinator.switchProfile(p.name)) {
                                is NetworkResult.Success -> {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.bot_selecteur_bascule, libelle(p.name, p)),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                    onChoisi()
                                }
                                is NetworkResult.Failure ->
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.bot_selecteur_echec, libelle(p.name, p), r.error.message),
                                        Toast.LENGTH_LONG,
                                    ).show()
                            }
                            bascule = null
                        }
                    },
                )
            }
        }
    }
}
