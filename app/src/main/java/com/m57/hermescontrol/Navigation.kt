package com.m57.hermescontrol

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.m57.hermescontrol.data.local.AuthManager
import com.m57.hermescontrol.data.update.WhatsNewNotice
import com.m57.hermescontrol.data.ws.ConnectionStatus
import com.m57.hermescontrol.data.ws.HermesWsClient
import com.m57.hermescontrol.profilelock.ProfileLock
import com.m57.hermescontrol.profilelock.ProfileLockEnforcer
import com.m57.hermescontrol.shortcuts.LauncherShortcutNavigationEffect
import com.m57.hermescontrol.theme.LocalHermesStatusColors
import com.m57.hermescontrol.ui.common.DisableDrawerGestures
import com.m57.hermescontrol.ui.common.DrawerGestureController
import com.m57.hermescontrol.ui.common.ExpandedSidebarBreakpoint
import com.m57.hermescontrol.ui.common.LocalDrawerGestureController
import com.m57.hermescontrol.ui.common.SidebarWidth
import com.m57.hermescontrol.ui.common.WhatsNewDialog
import com.m57.hermescontrol.ui.plugins.MemoryProviderDetailScreen
import com.m57.hermescontrol.ui.settings.ClientCertificatesPage
import com.m57.hermescontrol.ui.settings.SettingsAboutPage
import com.m57.hermescontrol.ui.settings.SettingsAppearancePage
import com.m57.hermescontrol.ui.settings.SettingsBehaviorPage
import com.m57.hermescontrol.ui.settings.SettingsChatPage
import com.m57.hermescontrol.ui.settings.SettingsConnectionPage
import com.m57.hermescontrol.ui.settings.SettingsLanguagePage
import com.m57.hermescontrol.ui.settings.SettingsVaultPage
import com.m57.hermescontrol.ui.settings.SettingsViewModel
import com.m57.hermescontrol.ui.settings.components.KO_FI_URL
import com.m57.hermescontrol.ui.toolsets.ToolsetDetailScreen
import kotlinx.coroutines.launch
import com.m57.hermescontrol.ui.authlogin.AuthLoginScreen as AuthLoginScreenContent
import com.m57.hermescontrol.ui.landing.LandingScreen as LandingScreenContent

private fun appEntryProvider(
    sessionId: String?,
    openDrawer: () -> Unit,
) = entryProvider {
    entry<LandingScreen> {
        // B7 (Jun 30 2026, kanban t_424): route landing screen buttons through navigateTo to prevent duplicate screens
        LandingScreenContent(
            locked =
                ProfileLock.state
                    .collectAsState()
                    .value.locked,
            onConnections = { NavigationController.navigateTo(ConnectionsScreen) },
            onAuthLogin = {
                NavigationController.navigateTo(AuthLoginScreen)
            },
        )
        // Landing doesn't use HermesScaffold — opt out of drawer gestures explicitly (issue #619).
        DisableDrawerGestures()
    }

    entry<AuthLoginScreen> {
        AuthLoginScreenContent(
            onConnected = {
                NavigationController.resetTo(ChatScreen)
            },
            onBack = {
                NavigationController.goBack()
            },
        )
        DisableDrawerGestures()
    }

    ScreenRegistry.ALL_SCREENS.forEach { screen ->
        addEntryProvider(clazz = screen.key::class) {
            screen.content(sessionId, openDrawer)
        }
    }

    // ── Settings drill-down sub-pages ───────────────────────────────────
    // Each passes drawerGesturesEnabled = false to HermesScaffold — this is the
    // single source of truth that prevents the drawer-scrim stuck-open bug
    // (issue #619). No global gesture set, no closeDrawer callback, no
    // LaunchedEffect(snapTo(Closed)) — the scaffold reconciles automatically.
    entry<ClientCertificatesScreen> {
        ClientCertificatesPage(onBack = { NavigationController.goBack() })
    }
    entry<SettingsConnection> {
        SettingsConnectionPage(
            onBack = { NavigationController.goBack() },
            onLogout = { /* handled by caller via goBack fallback */ },
            viewModel = viewModel { SettingsViewModel() },
        )
    }
    entry<SettingsAppearance> {
        SettingsAppearancePage(
            onBack = { NavigationController.goBack() },
            viewModel = viewModel { SettingsViewModel() },
        )
    }
    entry<SettingsLanguage> {
        SettingsLanguagePage(
            onBack = { NavigationController.goBack() },
            viewModel = viewModel { SettingsViewModel() },
        )
    }
    entry<SettingsChat> {
        SettingsChatPage(
            onBack = { NavigationController.goBack() },
            viewModel = viewModel { SettingsViewModel() },
        )
    }
    entry<SettingsBehavior> {
        SettingsBehaviorPage(
            onBack = { NavigationController.goBack() },
            viewModel = viewModel { SettingsViewModel() },
        )
    }
    entry<SettingsVault> {
        SettingsVaultPage(
            onBack = { NavigationController.goBack() },
        )
    }
    entry<SettingsAbout> {
        SettingsAboutPage(
            onBack = { NavigationController.goBack() },
        )
    }

    // ── Toolset detail drill-down (issue #782) ────────────────────────
    entry<ToolsetDetailKey> { key ->
        ToolsetDetailScreen(
            name = key.name,
            label = key.label,
            onBack = { NavigationController.goBack() },
        )
    }

    // ── Memory provider detail drill-down (issue #783) ─────────────────
    entry<MemoryProviderDetailKey> { key ->
        MemoryProviderDetailScreen(
            name = key.name,
            label = key.label,
            onBack = { NavigationController.goBack() },
        )
    }

    // ── Multi-Agent Group Chat Room ─────────────────────────────────────
    entry<GroupChatKey> { key ->
        com.m57.hermescontrol.ui.bots.group.GroupChatScreen(
            groupName = key.groupName,
            onBack = { NavigationController.goBack() },
        )
    }

    // ── Kanban Task Detail ─────────────────────────────────────────────
    entry<KanbanTaskDetailKey> { key ->
        com.m57.hermescontrol.ui.kanban.KanbanTaskScreen(
            boardSlug = key.boardSlug,
            taskId = key.taskId,
            onBack = { NavigationController.goBack() },
        )
    }
}

@Composable
fun MainNavigation(sessionId: String? = null) {
    val token by AuthManager.tokenFlow.collectAsState()
    val hasToken = !token.isNullOrBlank()
    val startScreen: NavKey = if (hasToken) ChatScreen else LandingScreen

    val backStack = remember(startScreen) { NavBackStack(startScreen) }
    NavigationController.backStack = backStack
    LauncherShortcutNavigationEffect(hasToken)
    ProfileLockEnforcer(hasToken)

    val pendingNewChat = NavigationController.pendingNewChatNavigation
    LaunchedEffect(pendingNewChat, hasToken) {
        if (pendingNewChat?.fromTile == true) {
            NavigationController.navigateTo(if (hasToken) ChatScreen else AuthLoginScreen)
        }
    }

    val pendingKanban = NavigationController.pendingKanbanNavigation
    LaunchedEffect(pendingKanban, hasToken) {
        if (hasToken && pendingKanban != null) {
            NavigationController.navigateTo(KanbanScreen)
            NavigationController.navigateTo(KanbanTaskDetailKey(pendingKanban.boardSlug, pendingKanban.taskId))
            NavigationController.consumePendingKanbanNavigation()
        }
    }

    val currentScreen = backStack.lastOrNull() ?: startScreen
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Single source of truth for drawer gestures (issue #619).
    // HermesScaffold / DisableDrawerGestures reconcile each screen's preference
    // into this controller via SideEffect; ModalNavigationDrawer reads .enabled
    // below. No DRAWER_GESTURE_SCREENS set, no LaunchedEffect(snapTo(Closed)),
    // no closeDrawer callback — the controller closes the drawer itself when a
    // screen opts out.
    val gestureController =
        remember(drawerState, scope) {
            DrawerGestureController(drawerState, scope)
        }

    // Wide windows (unfolded phones, tablets, split-screen) get a collapsible
    // permanent sidebar; narrow windows keep the modal drawer (issue #1607).
    // The NavDisplay stays at one composition position so resizing never
    // recreates the current screen.
    val wideWindow = hasToken && LocalConfiguration.current.screenWidthDp.dp >= ExpandedSidebarBreakpoint
    var sidebarVisible by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(wideWindow) {
        if (wideWindow) drawerState.close()
    }

    val openDrawer: () -> Unit = {
        if (wideWindow) {
            sidebarVisible = !sidebarVisible
        } else {
            scope.launch { drawerState.open() }
        }
    }

    val context = LocalContext.current
    var lastBackPressTime by remember { mutableLongStateOf(0L) }
    val backToastText = stringResource(R.string.press_back_again_to_exit)

    // Double-back to exit on root ChatScreen — unless the chat was opened from
    // a list (History, Bots): then the first back returns to that list (Telegram
    // chat-list parity) and only a further double-back exits.
    BackHandler(enabled = currentScreen == ChatScreen && backStack.size == 1) {
        // Fourche noronuke : depuis une conversation ouverte, le retour mène toujours à la liste des
        // conversations ; seule une conversation neuve (vide) garde la double pression pour sortir.
        if (NavigationController.chatSessionOpen) {
            NavigationController.consumeChatReturnScreen()
            NavigationController.navigateTo(HistoryScreen)
        } else {
            val now = System.currentTimeMillis()
            if (now - lastBackPressTime < 2000L) {
                (context as? Activity)?.finish()
            } else {
                lastBackPressTime = now
                Toast.makeText(context, backToastText, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fourche noronuke : le retour depuis la liste des conversations ouvre une conversation neuve.
    BackHandler(enabled = currentScreen == HistoryScreen) {
        NavigationController.openNewChat()
    }

    // Safety fallback: if a non-chat screen somehow sits alone on stack, swipe back returns to ChatScreen
    BackHandler(
        enabled = currentScreen != ChatScreen && currentScreen != LandingScreen && backStack.size == 1,
    ) {
        NavigationController.goBack()
    }

    CompositionLocalProvider(LocalDrawerGestureController provides gestureController) {
        AdaptiveNavigationShell(
            wideWindow = wideWindow,
            sidebarVisible = sidebarVisible,
            drawerState = drawerState,
            gesturesEnabled = gestureController.enabled,
            drawerBody = { onItemClick ->
                NavigationDrawerBody(
                    currentScreen = currentScreen,
                    hasToken = hasToken,
                    onItemClick = onItemClick,
                )
            },
            onModalItemClick = { scope.launch { drawerState.close() } },
        ) {
            Scaffold(
                contentWindowInsets = WindowInsets.navigationBars,
            ) { paddingValues ->
                NavDisplay(
                    backStack = backStack,
                    onBack = { NavigationController.goBack() },
                    entryProvider =
                        appEntryProvider(sessionId, openDrawer),
                    modifier =
                        Modifier
                            .padding(paddingValues)
                            .consumeWindowInsets(paddingValues)
                            .fillMaxSize(),
                )
            }
        }

        WhatsNewNotice.release?.let { release ->
            WhatsNewDialog(
                tag = release.tagName,
                releaseNotes = release.body,
                onDismiss = { WhatsNewNotice.dismiss() },
            )
        }
    }
}

/** Fourche noronuke : largeur du tiroir modal, menu court. */
private val TiroirLargeur = 280.dp

/** Fourche noronuke : icônes au trait (famille « Outlined ») pour les cinq entrées du menu. */
private fun iconeDuMenu(key: androidx.navigation3.runtime.NavKey): androidx.compose.ui.graphics.vector.ImageVector? =
    when (key) {
        ChatScreen -> Icons.Outlined.ChatBubbleOutline
        HistoryScreen -> Icons.Outlined.Forum
        ProjectsScreen -> Icons.Outlined.FolderOpen
        ModelScreen -> Icons.Outlined.Psychology
        SettingsScreen -> Icons.Outlined.Settings
        else -> null
    }

/**
 * Wide windows show [drawerBody] in a permanent sidebar; narrow windows show it in a modal
 * drawer. The modal drawer stays composed in both modes so resizing never recreates [content].
 */
@Composable
internal fun AdaptiveNavigationShell(
    wideWindow: Boolean,
    sidebarVisible: Boolean,
    drawerState: DrawerState,
    gesturesEnabled: Boolean,
    drawerBody: @Composable (onItemClick: () -> Unit) -> Unit,
    onModalItemClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        if (wideWindow && sidebarVisible) {
            PermanentDrawerSheet(
                modifier = Modifier.width(SidebarWidth).fillMaxHeight().verticalScroll(rememberScrollState()),
            ) {
                drawerBody {}
            }
        }
        // A closed modal sheet sits at -sheetWidth inside this Box. Without the clip it draws
        // over the permanent sidebar and pushes its labels off-screen (issues #1639, #1640).
        Box(modifier = Modifier.weight(1f).fillMaxHeight().clipToBounds()) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = gesturesEnabled && !wideWindow,
                drawerContent = {
                    // Fourche noronuke : le défilement porté par la feuille elle-même la réduisait à la hauteur
                    // de son contenu ; il passe dans une colonne intérieure pour que le tiroir prenne tout
                    // l'écran, et la feuille est plus étroite puisque le menu est court.
                    ModalDrawerSheet(
                        modifier = Modifier.width(TiroirLargeur).fillMaxHeight(),
                    ) {
                        Column(modifier = Modifier.fillMaxHeight().verticalScroll(rememberScrollState())) {
                            drawerBody(onModalItemClick)
                        }
                    }
                },
                content = content,
            )
        }
    }
}

/** Drawer content shared by the modal drawer (narrow) and the permanent sidebar (wide). */
@Composable
internal fun NavigationDrawerBody(
    currentScreen: NavKey,
    hasToken: Boolean,
    onItemClick: () -> Unit,
) {
    val context = LocalContext.current
    val connectionStatus by HermesWsClient.connectionStatus.collectAsState()
    val statusColor =
        when (connectionStatus) {
            ConnectionStatus.CONNECTED -> LocalHermesStatusColors.current.success

            ConnectionStatus.CONNECTING,
            ConnectionStatus.RECONNECTING,
            -> LocalHermesStatusColors.current.warning

            ConnectionStatus.DISCONNECTED,
            ConnectionStatus.NO_NETWORK,
            ConnectionStatus.AUTH_EXPIRED,
            -> LocalHermesStatusColors.current.error
        }
    // Fourche noronuke : en-tête orné du couvercle de piano de la charte (illustration du site,
    // 33 Ko), titre à l'encre et pastille de connexion ; un filet sépare le menu.
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 16.dp, bottom = 16.dp),
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(R.drawable.fourche_illu_couvercle),
            contentDescription = null,
            modifier = Modifier.size(width = 50.dp, height = 56.dp),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.nav_drawer_title),
                    style =
                        MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier =
                        Modifier
                            .size(8.dp)
                            .background(color = statusColor, shape = CircleShape),
                )
            }
            Text(
                text = stringResource(R.string.nav_drawer_subtitle),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        color = MaterialTheme.colorScheme.outlineVariant,
    )
    Spacer(modifier = Modifier.height(8.dp))

    // #1643: a profile lock keeps only its allowed destinations; drop now-empty section headers too.
    val lockState by ProfileLock.state.collectAsState()
    // Fourche noronuke : liste plate et courte (voir Allegement), sans titres de section.
    run {
        val entries =
            Allegement.dansLeMenu.mapNotNull { key -> ScreenRegistry.ALL_SCREENS.firstOrNull { it.key == key } }
                .filter { hasToken && (!lockState.locked || ProfileLock.allows(it.key)) }
        entries
            .forEach { entry ->
                // Fourche noronuke : marge intérieure de Material, pour que la pilule de sélection
                // ne vienne plus se couper contre le bord du tiroir ; icônes au trait, d'une seule famille.
                NavigationDrawerItem(
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    icon = { Icon(iconeDuMenu(entry.key) ?: entry.icon, contentDescription = null) },
                    label = { Text(stringResource(entry.labelRes)) },
                    selected = currentScreen == entry.key,
                    onClick = {
                        onItemClick()
                        NavigationController.navigateTo(entry.key)
                    },
                    colors =
                        NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                )
            }
    }

    // Fourche noronuke : bouton de don retiré.
    Spacer(modifier = Modifier.height(12.dp))
}
