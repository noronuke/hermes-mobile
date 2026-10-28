package com.m57.hermescontrol.ui.chat

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RefusalReasonTest {
    @Test
    fun `reads the machine reason of a 4090 refusal`() {
        val data = Json.parseToJsonElement("""{"reason":"SESSION_NOT_OWNED","session_id":"abc"}""")
        assertEquals(SESSION_NOT_OWNED, refusalReasonOf(data))
    }

    @Test
    fun `null when the refusal carries no reason`() {
        assertNull(refusalReasonOf(Json.parseToJsonElement("""{"message":"no"}""")))
        assertNull(refusalReasonOf(null))
        assertNull(refusalReasonOf(Json.parseToJsonElement(""""plain string"""")))
    }
}
