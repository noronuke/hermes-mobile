package com.m57.hermescontrol.ui.sessions.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.m57.hermescontrol.R
import com.m57.hermescontrol.theme.LocalSpacing

/**
 * Fourche noronuke : en-tête d'une section de la liste des conversations par provenance
 * (Desktop, Signal, Webhooks…), comme le panneau latéral du Desktop. [key] est `main` pour la
 * liste principale, sinon la clé d'un [com.m57.hermescontrol.ui.sessions.SourceGroup].
 */
@Composable
fun SourceGroupHeader(
    key: String,
    count: Int?,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val label =
        stringResource(
            when (key) {
                "signal" -> R.string.fourche_group_signal
                "webhook" -> R.string.fourche_group_webhook
                "cron" -> R.string.fourche_group_cron
                "oneshot" -> R.string.fourche_group_oneshot
                "agents" -> R.string.fourche_group_agents
                "banc" -> R.string.fourche_group_banc
                else -> R.string.fourche_group_main
            },
        )
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClick = onToggle)
                .padding(vertical = spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (count != null) "$label ($count)" else label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(spacing.xs))
        Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription =
                stringResource(
                    if (expanded) R.string.fourche_group_collapse else R.string.fourche_group_expand,
                ),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}
