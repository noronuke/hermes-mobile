package com.m57.hermescontrol.ui.sessions.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.getValue
import androidx.compose.foundation.border
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.m57.hermescontrol.R
import com.m57.hermescontrol.data.model.SessionLiveStatus
import com.m57.hermescontrol.theme.LocalHermesStatusColors
import com.m57.hermescontrol.theme.LocalSpacing
import com.m57.hermescontrol.ui.common.StatusBadgeType
import com.m57.hermescontrol.ui.common.statusBadgeColors

@Composable
fun SessionLiveStatusIndicator(
    liveStatus: SessionLiveStatus,
    sessionId: String,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val statusColors = LocalHermesStatusColors.current

    val badgeType = liveStatus.badgeType()
    val (textRes, descRes) = liveStatus.labels()

    // Fourche noronuke : étiquette discrète en puce ronde, teinte légère de la couleur d'état
    // (le fond plein criait à côté de la pastille, qui dit déjà la même chose). Le texte garde
    // l'encre de la carte ; seule l'icône porte la couleur, pour un contraste AA sûr.
    val (fill, _) = requireNotNull(statusBadgeColors(badgeType, statusColors))
    val ink = MaterialTheme.colorScheme.onSurface
    val text = stringResource(textRes)
    val description = stringResource(descRes)

    Surface(
        modifier =
            modifier
                .testTag("session_live_status_$sessionId")
                .semantics { contentDescription = description },
        shape = com.m57.hermescontrol.theme.Formes.Puce,
        color = fill.copy(alpha = 0.12f),
    ) {
        Row(
            modifier =
                Modifier.padding(
                    horizontal = spacing.sm + 4.dp,
                    vertical = 3.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            if (liveStatus == SessionLiveStatus.WORKING) {
                CircularProgressIndicator(
                    modifier = Modifier.size(10.dp),
                    strokeWidth = 1.5.dp,
                    color = fill,
                )
            } else {
                Icon(
                    imageVector = liveStatus.icon(),
                    contentDescription = null,
                    tint = fill,
                    modifier = Modifier.size(12.dp),
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = ink,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

/**
 * Issue #1642: color tells urgency, the label tells the reason. Every "needs you" state shares the
 * warning color; Done is info (blue), Failed is error (red).
 */
internal fun SessionLiveStatus.badgeType(): StatusBadgeType =
    when (this) {
        // Fourche noronuke : comme l'appli de bureau, bleu pendant la réflexion, vert une fois fini.
        SessionLiveStatus.WORKING -> StatusBadgeType.INFO

        SessionLiveStatus.WAITING,
        SessionLiveStatus.WAITING_APPROVAL,
        SessionLiveStatus.WAITING_CLARIFY,
        -> StatusBadgeType.WARNING

        SessionLiveStatus.DONE -> StatusBadgeType.SUCCESS

        SessionLiveStatus.FAILED -> StatusBadgeType.ERROR
    }

/** (label, accessibility description) string resources. */
private fun SessionLiveStatus.labels(): Pair<Int, Int> =
    when (this) {
        SessionLiveStatus.WORKING -> {
            R.string.sessions_live_status_running to R.string.sessions_live_status_running_desc
        }

        SessionLiveStatus.WAITING -> {
            R.string.sessions_live_status_waiting to R.string.sessions_live_status_waiting_desc
        }

        SessionLiveStatus.WAITING_APPROVAL -> {
            R.string.sessions_live_status_approval to
                R.string.sessions_live_status_approval_desc
        }

        SessionLiveStatus.WAITING_CLARIFY -> {
            R.string.sessions_live_status_clarify to
                R.string.sessions_live_status_clarify_desc
        }

        SessionLiveStatus.DONE -> {
            R.string.sessions_live_status_done to R.string.sessions_live_status_done_desc
        }

        SessionLiveStatus.FAILED -> {
            R.string.sessions_live_status_failed to R.string.sessions_live_status_failed_desc
        }
    }

private fun SessionLiveStatus.icon(): ImageVector =
    when (this) {
        SessionLiveStatus.DONE -> Icons.Filled.CheckCircle
        SessionLiveStatus.FAILED -> Icons.Filled.Error
        else -> Icons.Filled.Warning
    }


/**
 * Fourche noronuke : pastille ronde de 8 dp devant le titre d'une conversation.
 *
 * Un processus en arrière-plan encore vivant ([enArrierePlan]) donne un anneau gris qui clignote,
 * comme sur le bureau : il l'emporte sur « fini, pas lu », mais cède devant un tour en cours ou une
 * attente de réponse, qui disent davantage.
 */
@androidx.compose.runtime.Composable
internal fun SessionStatusDot(
    liveStatus: SessionLiveStatus?,
    sessionId: String,
    modifier: Modifier = Modifier,
    enArrierePlan: Boolean = false,
) {
    val statusColors = LocalHermesStatusColors.current
    val arrierePlanVisible =
        enArrierePlan &&
            (liveStatus == null || liveStatus == SessionLiveStatus.DONE || liveStatus == SessionLiveStatus.FAILED)
    if (arrierePlanVisible) {
        val transition = androidx.compose.animation.core.rememberInfiniteTransition(label = "arriere_plan")
        val alpha by transition.animateFloat(
            initialValue = 1f,
            targetValue = 0.25f,
            animationSpec =
                androidx.compose.animation.core.infiniteRepeatable(
                    animation = androidx.compose.animation.core.tween(durationMillis = 900),
                    repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
                ),
            label = "arriere_plan_alpha",
        )
        val grey = androidx.compose.material3.MaterialTheme.colorScheme.outline
        androidx.compose.foundation.layout.Box(
            modifier =
                modifier
                    .testTag("session_status_dot_$sessionId")
                    .size(10.dp)
                    .graphicsLayer { this.alpha = alpha }
                    .border(2.dp, grey, CircleShape),
        )
        return
    }
    // Fourche noronuke : pastille de 10 dp ; au repos, gris sauge adouci (l'ancien filet était invisible).
    val color =
        liveStatus?.let { statusBadgeColors(it.badgeType(), statusColors)?.first }
            ?: androidx.compose.material3.MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
    androidx.compose.foundation.layout.Box(
        modifier =
            modifier
                .testTag("session_status_dot_$sessionId")
                .size(10.dp)
                .background(color, CircleShape),
    )
}
