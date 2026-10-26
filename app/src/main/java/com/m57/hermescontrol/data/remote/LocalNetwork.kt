package com.m57.hermescontrol.data.remote

import java.net.InetAddress
import java.net.URI

/**
 * Android 17 (API 37) gates local-network access behind the
 * `ACCESS_LOCAL_NETWORK` runtime permission. The platform's own definition of
 * "local network" excludes loopback (127/8) and covers private RFC1918 ranges,
 * link-local (169.254/16), and CGNAT (100.64/10).
 *
 * Connections to loopback (the default `127.0.0.1:9119` gateway on-device) and
 * to public/remote hosts do NOT require the permission — only true LAN hosts do.
 *
 * @return true when [host] resolves to an address Android 17 gates behind local-network permission.
 */
fun isLocalNetworkHost(host: String): Boolean {
    // Fourche noronuke : un nom MagicDNS de Tailscale (`*.ts.net`) désigne toujours une adresse
    // 100.64/10. On le décide sur le nom, sans résolution : si la résolution échoue au lancement
    // (VPN pas encore prêt), l'ancienne règle concluait « pas de réseau local », la garde de
    // démarrage laissait passer sans la permission et Android 17 bloquait ensuite la connexion
    // en silence (« Non connecté » sans aucune requête reçue par le NUC, 26/10/2026).
    if (isTailscaleMagicDnsName(host)) return true
    val addr =
        runCatching { InetAddress.getByName(host) }.getOrNull() ?: return false
    if (addr.isLoopbackAddress) return false
    val raw = addr.address
    if (raw.size != 4) return false // IPv6: not LAN-gated by the legacy ranges below
    val a = raw[0].toInt() and 0xFF
    val b = raw[1].toInt() and 0xFF
    return when {
        a == 10 -> true

        // 10.0.0.0/8
        a == 172 && b in 16..31 -> true

        // 172.16.0.0/12
        a == 192 && b == 168 -> true

        // 192.168.0.0/16
        a == 169 && b == 254 -> true

        // 169.254.0.0/16 link-local
        a == 100 && b in 64..127 -> true

        // 100.64.0.0/10 CGNAT
        else -> false
    }
}

/**
 * Whether connecting to [baseUrl] requires the `ACCESS_LOCAL_NETWORK` permission
 * on Android 17+. Extracted as a pure function so the connect flow can be gated
 * (and unit-tested) without a real permission check.
 *
 * Uses [java.net.URI] rather than [android.net.Uri] because the latter is a
 * non-functional stub under plain JVM unit tests (its `host` returns null).
 */
fun needsLocalNetworkPermission(baseUrl: String): Boolean {
    val host = runCatching { URI(baseUrl).host }.getOrNull() ?: return false
    return isLocalNetworkHost(host)
}

/**
 * Gate decision for the connect flow: on Android 17+ (API 37) a LAN gateway host
 * requires the `ACCESS_LOCAL_NETWORK` runtime permission; earlier APIs and
 * loopback/public hosts do not. Pure so it can be unit-tested without mocking
 * [android.os.Build.VERSION.SDK_INT].
 */
fun requiresLocalNetworkPermission(
    sdkInt: Int,
    baseUrl: String,
): Boolean = sdkInt >= 37 && needsLocalNetworkPermission(baseUrl)

/** Fourche noronuke : nom MagicDNS de Tailscale (`machine.tailnet.ts.net`), adresse 100.64/10. */
fun isTailscaleMagicDnsName(host: String): Boolean {
    val h = host.trim().trimEnd('.').lowercase()
    return h.endsWith(".ts.net") && h.length > ".ts.net".length
}
