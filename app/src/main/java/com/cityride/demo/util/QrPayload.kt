package com.cityride.demo.util

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Payload encoded into QR codes issued by this demo app:
 * `CITYRIDE-DEMO|<ticketId>|<activatedAtMillis>|<signature>`.
 */
object QrPayload {
    private const val PREFIX = "CITYRIDE-DEMO"
    private const val SEPARATOR = "|"
    private val key = "cityride-demo-local-key".toByteArray()

    fun create(ticketId: String, activatedAtMillis: Long): String {
        val body = listOf(PREFIX, ticketId, activatedAtMillis.toString()).joinToString(SEPARATOR)
        return body + SEPARATOR + sign(body)
    }

    /** Returns the ticket id when [payload] was issued by this app, otherwise null. */
    fun parseTicketId(payload: String): String? {
        val parts = payload.split(SEPARATOR)
        if (parts.size != 4 || parts[0] != PREFIX) return null
        val body = parts.take(3).joinToString(SEPARATOR)
        return parts[1].takeIf { sign(body) == parts[3] }
    }

    private fun sign(body: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key, "HmacSHA256"))
        return mac.doFinal(body.toByteArray()).joinToString("") { "%02x".format(it) }.take(16)
    }
}
