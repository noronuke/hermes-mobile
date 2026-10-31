package com.m57.hermescontrol.ui.sessions

import com.m57.hermescontrol.data.model.SessionInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SessionSourceGroupsTest {
    private val rows =
        listOf(
            SessionInfo("d1", source = "desktop"),
            SessionInfo("s1", source = "signal"),
            SessionInfo("o1", source = "oneshot"),
            SessionInfo("c1", source = "cron"),
            SessionInfo("a1", source = "a2a"),
            SessionInfo("b1", source = "banc"),
            SessionInfo("t1", source = "telegram"),
        )

    @Test
    fun mainList_keepsDesktopAndUnknownSources_only() {
        val state = SessionsUiState(sessions = rows)
        assertEquals(listOf("d1", "t1"), state.displaySessions.map { it.id })
    }

    @Test
    fun groupSessions_returnsRowsOfThatGroup() {
        val state = SessionsUiState(sessions = rows)
        assertEquals(listOf("a1"), state.groupSessions("agents").map { it.id })
        assertEquals(listOf("s1"), state.groupSessions("signal").map { it.id })
        assertEquals(emptyList<String>(), state.groupSessions("webhook").map { it.id })
    }

    @Test
    fun sourceGroupOf_isCaseInsensitiveAndNullSafe() {
        assertEquals("signal", sourceGroupOf("Signal")?.key)
        assertNull(sourceGroupOf("desktop"))
        assertNull(sourceGroupOf(null))
    }

    @Test
    fun excludedSources_matchTheGroups() {
        assertEquals(
            SOURCE_GROUPS.flatMap { it.sources }.toSet(),
            EXCLUS_LISTE_PRINCIPALE.split(",").toSet(),
        )
    }
}
