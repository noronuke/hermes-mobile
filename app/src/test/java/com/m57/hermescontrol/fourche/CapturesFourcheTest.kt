package com.m57.hermescontrol.fourche

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.m57.hermescontrol.R
import com.m57.hermescontrol.data.model.SessionInfo
import com.m57.hermescontrol.data.model.SessionLiveStatus
import com.m57.hermescontrol.data.ws.CommandCatalog
import com.m57.hermescontrol.theme.HermesControlTheme
import com.m57.hermescontrol.theme.ThemePreference
import com.m57.hermescontrol.ui.chat.ChatMessage
import com.m57.hermescontrol.ui.chat.MessageRole
import com.m57.hermescontrol.ui.chat.UserBubble
import com.m57.hermescontrol.ui.chat.components.ChatInputBar
import com.m57.hermescontrol.ui.chat.components.ReasoningCard
import com.m57.hermescontrol.ui.chat.fullbleed.FullBleedAgentMessage
import com.m57.hermescontrol.ui.common.EmptyState
import com.m57.hermescontrol.ui.common.SearchBar
import com.m57.hermescontrol.ui.sessions.components.PinnedSectionHeader
import com.m57.hermescontrol.ui.sessions.components.SessionCard
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * Fourche noronuke : rendu hors appareil des principaux écrans, en clair et en sombre, pour les
 * captures avant/après de la refonte. Ne tourne que si CAPTURES_FOURCHE=1 (fabrique d'essai).
 * Données fictives : aucun titre réel de conversation.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h923dp-xxhdpi", application = android.app.Application::class)
class CapturesFourcheTest {
    @get:Rule val compose = createComposeRule()

    private val sortie = File(System.getProperty("user.dir"), "build/captures-fourche").apply { mkdirs() }
    private val maintenant = 1_792_900_000_000L

    @Before fun seulementSurDemande() = assumeTrue(System.getenv("CAPTURES_FOURCHE") == "1")

    private fun capture(
        nom: String,
        sombre: Boolean,
        contenu: @Composable () -> Unit,
    ) {
        compose.setContent {
            HermesControlTheme(themePreference = if (sombre) ThemePreference.DARK else ThemePreference.LIGHT) {
                Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) { contenu() }
            }
        }
        compose.waitForIdle()
        val image = compose.onRoot().captureToImage().asAndroidBitmap()
        File(sortie, "$nom-${if (sombre) "sombre" else "clair"}.png").outputStream().use {
            image.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    private fun session(
        id: String,
        titre: String,
        apercu: String,
        n: Int,
        modele: String = "deepseek/deepseek-v4.1-flash-1017",
    ) = SessionInfo(
        id = id,
        title = titre,
        preview = apercu,
        message_count = n,
        model = modele,
        source = "mobile",
        last_active = (maintenant / 1000 - 600).toDouble(),
        started_at = (maintenant / 1000 - 3600).toDouble(),
    )

    @Composable
    private fun Carte(
        s: SessionInfo,
        etat: SessionLiveStatus? = null,
        epinglee: Boolean = false,
        arrierePlan: Boolean = false,
    ) = SessionCard(
        session = s,
        displayTitle = s.title.orEmpty(),
        branchStem = null,
        query = "",
        isSelecting = false,
        isSelected = false,
        isDeleting = false,
        isPinned = epinglee,
        liveStatus = etat,
        enArrierePlan = arrierePlan,
        nowMillis = maintenant,
        highlightBackground = Color.Yellow,
        highlightForeground = Color.Black,
        onCardClick = {},
        onToggleSelection = {},
        onSelect = {},
        onRename = {},
        onTogglePin = {},
        onToggleArchive = {},
        onDelete = {},
    )

    @Composable
    private fun Liste() {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 12.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("Conversations", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            SearchBar(query = "", onQueryChange = {}, placeholder = "Rechercher des conversations…")
            PinnedSectionHeader(count = 1, expanded = true, onToggle = {})
            Carte(session("a", "Devis du Pleyel de 1920", "Prépare le devis d'accord et de réglage", 24, "anthropic/claude-sonnet-5.5"), epinglee = true)
            Text("Récentes", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Carte(session("b", "Tournée de novembre en Balagne", "Classe les rendez-vous par village", 12), etat = SessionLiveStatus.WORKING)
            Carte(session("c", "Relance du conservatoire", "Rédige une relance courte et polie", 6), etat = SessionLiveStatus.DONE)
            Carte(session("d", "Humidité dans la salle des fêtes", "Que conseiller pour l'hiver ?", 9), arrierePlan = true)
            Carte(session("e", "Tempérament pour un clavecin", "Compare Vallotti et Werckmeister III", 31))
        }
    }

    @Composable
    private fun Saisie() {
        ChatInputBar(
            inputState = TextFieldState(),
            onSend = {},
            onMicTap = {},
            isListening = false,
            isAgentTyping = false,
            isConnected = true,
            commandCatalog = CommandCatalog(),
            isSessionReady = true,
            currentSessionModel = "claude-sonnet-5.5",
            reasoningLevel = "medium",
        )
    }

    @Composable
    private fun Conversation() {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Column(modifier = Modifier.weight(1f).padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                UserBubble(
                    message =
                        ChatMessage(
                            role = MessageRole.USER,
                            content = "Peux-tu me préparer le devis pour le Pleyel de Rutali ?",
                            timestamp = maintenant - 120_000,
                        ),
                )
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ReasoningCard(reasoningText = "Je reprends le barème d'accord et l'état décrit par le client.")
                }
                FullBleedAgentMessage(
                    message =
                        ChatMessage(
                            role = MessageRole.ASSISTANT,
                            content =
                                "Voici une proposition.\n\n**Accord et réglage** : 220 €, visite de diagnostic comprise.\n\n" +
                                    "- remplacement de deux cordes de basse ;\n- harmonisation légère des marteaux.\n\n" +
                                    "Veux-tu que je l'envoie au format `PDF` ?",
                            timestamp = maintenant - 60_000,
                        ),
                )
            }
            Saisie()
        }
    }

    @Composable
    private fun Accueil() {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                EmptyState(
                    title = "Prêt à discuter",
                    subtitle = "Envoyez un message pour démarrer une conversation",
                    illustration = painterResource(R.drawable.fourche_illu_pianiste),
                )
            }
            Spacer(Modifier.padding(4.dp))
            Saisie()
        }
    }

    @Composable
    private fun Tiroir() {
        androidx.compose.material3.ModalDrawerSheet(
            modifier = Modifier.width(280.dp).statusBarsPadding(),
        ) {
            Column {
                com.m57.hermescontrol.NavigationDrawerBody(
                    currentScreen = com.m57.hermescontrol.ChatScreen,
                    hasToken = true,
                    onItemClick = {},
                )
            }
        }
    }

    @Test fun tiroirClair() = capture("02-tiroir", false) { Tiroir() }

    @Test fun tiroirSombre() = capture("02-tiroir", true) { Tiroir() }

    @Test fun listeClair() = capture("03-liste", false) { Liste() }

    @Test fun listeSombre() = capture("03-liste", true) { Liste() }

    @Test fun conversationClair() = capture("04-conversation", false) { Conversation() }

    @Test fun conversationSombre() = capture("04-conversation", true) { Conversation() }

    @Test fun accueilClair() = capture("01-nouvelle", false) { Accueil() }

    @Test fun accueilSombre() = capture("01-nouvelle", true) { Accueil() }
}
