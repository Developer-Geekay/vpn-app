package com.geekay.vpnapp.vpn

import android.util.Log
import com.geekay.vpnapp.data.local.TokenManager
import com.geekay.vpnapp.data.network.ApiService
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import com.wireguard.config.Interface
import com.wireguard.config.Peer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VpnManager @Inject constructor(
    private val backend: GoBackend,
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    private val tunnelName = "wg0"
    private val tunnel = MyTunnel(tunnelName)

    suspend fun connect() = withContext(Dispatchers.IO) {
        try {
            val config = fetchConfig()
            if (config != null) {
                backend.setState(tunnel, Tunnel.State.UP, config)
                Log.d("VpnManager", "VPN Connected successfully")
                true
            } else {
                Log.e("VpnManager", "Failed to fetch VPN config")
                false
            }
        } catch (e: Exception) {
            Log.e("VpnManager", "Failed to connect to VPN", e)
            false
        }
    }

    suspend fun disconnect() = withContext(Dispatchers.IO) {
        try {
            backend.setState(tunnel, Tunnel.State.DOWN, null)
            Log.d("VpnManager", "VPN Disconnected successfully")
            true
        } catch (e: Exception) {
            Log.e("VpnManager", "Failed to disconnect VPN", e)
            false
        }
    }

    // Fetches config from API and parses it to WireGuard Config
    private suspend fun fetchConfig(): Config? {
        val token = tokenManager.getToken() ?: return null
        val response = apiService.getVpnConfig("Bearer $token")
        
        if (response.isSuccessful) {
            val data = response.body() ?: return null
            
            val interfaceBuilder = Interface.Builder()
                .parsePrivateKey(data.interfacePrivateKey)
                .parseAddresses(data.interfaceAddress)
                .parseDnsServers("8.8.8.8")

            val peerBuilder = Peer.Builder()
                .parsePublicKey(data.peerPublicKey)
                .parseEndpoint(data.peerEndpoint)
                .parseAllowedIPs(data.peerAllowedIps)

            return Config.Builder()
                .setInterface(interfaceBuilder.build())
                .addPeer(peerBuilder.build())
                .build()
        }
        return null
    }
}
