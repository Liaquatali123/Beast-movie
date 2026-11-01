package com.example.data.api

import com.example.BuildConfig
import java.io.ByteArrayOutputStream

/**
 * Secure internal network configuration.
 * All sensitive keys and endpoints are XOR-obfuscated in bytecode to prevent extraction.
 */
object ApiConfig {

    private const val KEY_MASK: Int = 0x5A
    private const val B64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

    private fun decodeMasked(encoded: String): String {
        return try {
            val clean = encoded.trimEnd('=')
            val out = ByteArrayOutputStream()
            var buffer = 0
            var bits = 0
            for (ch in clean) {
                val idx = B64_CHARS.indexOf(ch)
                if (idx >= 0) {
                    buffer = (buffer shl 6) or idx
                    bits += 6
                    if (bits >= 8) {
                        bits -= 8
                        val b = ((buffer shr bits) and 0xFF) xor KEY_MASK
                        out.write(b)
                    }
                }
            }
            out.toString("UTF-8")
        } catch (_: Throwable) {
            ""
        }
    }

    private val MASKED_BASE_URL = "Mi4uKilgdXUyb3c7KjN0OzU0Pyg1NTd0OTU3dQ=="
    private val MASKED_HOST = "NzUsMz93ODUidDk1"
    private val MASKED_USER_AGENT = "FzUgMzY2O3VvdGp6chYzNC8iYXobND4oNTM+emtrYXosMyw1emtjb2t6GC8zNj51CAprG3RoamptaGp0amtoc3obKio2Pw0/OBEzLnVvaW10aWx6chESDhcWdno2MzE/eh0/OTE1c3oMPygpMzU0dW50anoZMig1Nz91a29udGp0YmppbXRram16FzU4MzY/egk7PDsoM3VvaW10aWw="
    private val MASKED_REFERER = "Mi4uKilgdXU3NSwzP3c4NSJ0OTV1"
    private val MASKED_ORIGIN = "Mi4uKilgdXU3NSwzP3c4NSJ0OTU="
    private val MASKED_X_U_AID = "aDxrP247aG93aTk+a3dubDhpd2NsP2J3a2o+Yj5obWhpamlj"
    private val MASKED_CLIENT_INFO = "IXguMzc/IDU0P3hgeBs3PygzOTt1FD8tBQM1KDF4Jw=="
    private val MASKED_REQUEST_LANG = "PzQ="
    private val MASKED_VIP_RESTRICT = "aw=="
    private val MASKED_NO_HIGH_RISK_RESTRICT = "ag=="
    private val MASKED_AUTH_TOKEN = "GD87KD8oej8jEDI4HTkzFTMQEw8gE2sUMxMpEzQIbzkZE2wTMSoCDBkQY3Q/IxBrOw0LMxUwOSMVDj0gFCAPaBceF2oXIA9qFQ4TbxcOEykTNxxqORkTbBcjLTMAAjJqEzA1MxcOOW8XMBNrFA4XLRcJEykTNwxuORkTbBcOPS0XHhsgFw4XLRcJLTM7DRxqEzA1IhQgMSMXMA9rFx4bIjwLdCM/OCAQGzMpKXcwIjwyOD8oIhRrAD84a2geam1jF2MjAzUcIwozDz4cBT0="

    val baseUrl: String
        get() = try {
            val v = BuildConfig::class.java.getField("API_BASE_URL").get(null) as? String
            if (!v.isNullOrBlank()) v else decodeMasked(MASKED_BASE_URL)
        } catch (_: Throwable) {
            decodeMasked(MASKED_BASE_URL)
        }

    val host: String
        get() = try {
            val v = BuildConfig::class.java.getField("API_HOST").get(null) as? String
            if (!v.isNullOrBlank()) v else decodeMasked(MASKED_HOST)
        } catch (_: Throwable) {
            decodeMasked(MASKED_HOST)
        }

    val userAgent: String
        get() = try {
            val v = BuildConfig::class.java.getField("API_USER_AGENT").get(null) as? String
            if (!v.isNullOrBlank()) v else decodeMasked(MASKED_USER_AGENT)
        } catch (_: Throwable) {
            decodeMasked(MASKED_USER_AGENT)
        }

    val referer: String
        get() = try {
            val v = BuildConfig::class.java.getField("API_REFERER").get(null) as? String
            if (!v.isNullOrBlank()) v else decodeMasked(MASKED_REFERER)
        } catch (_: Throwable) {
            decodeMasked(MASKED_REFERER)
        }

    val origin: String
        get() = try {
            val v = BuildConfig::class.java.getField("API_ORIGIN").get(null) as? String
            if (!v.isNullOrBlank()) v else decodeMasked(MASKED_ORIGIN)
        } catch (_: Throwable) {
            decodeMasked(MASKED_ORIGIN)
        }

    val xUAid: String
        get() = try {
            val v = BuildConfig::class.java.getField("API_X_U_AID").get(null) as? String
            if (!v.isNullOrBlank()) v else decodeMasked(MASKED_X_U_AID)
        } catch (_: Throwable) {
            decodeMasked(MASKED_X_U_AID)
        }

    val authToken: String
        get() = try {
            val v = BuildConfig::class.java.getField("API_AUTH_TOKEN").get(null) as? String
            if (!v.isNullOrBlank()) v else decodeMasked(MASKED_AUTH_TOKEN)
        } catch (_: Throwable) {
            decodeMasked(MASKED_AUTH_TOKEN)
        }

    val clientInfo: String get() = decodeMasked(MASKED_CLIENT_INFO)
    val requestLang: String get() = decodeMasked(MASKED_REQUEST_LANG)
    val vipRestrict: String get() = decodeMasked(MASKED_VIP_RESTRICT)
    val noHighRiskRestrict: String get() = decodeMasked(MASKED_NO_HIGH_RISK_RESTRICT)
}
