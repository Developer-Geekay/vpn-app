package com.geekay.vpnapp.vpn

import android.content.Intent
import android.net.VpnService
import android.util.Log
import com.wireguard.android.backend.Tunnel
import com.wireguard.android.backend.GoBackend
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WireGuardVpnService : VpnService() {

    private val backend by lazy { GoBackend(this) }
    
    override fun onCreate() {
        super.onCreate()
        Log.d("VpnService", "VPN Service Created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("VpnService", "VPN Service Destroyed")
    }
}

class MyTunnel(private val name: String) : Tunnel {
    override fun getName(): String = name
    
    override fun onStateChange(newState: Tunnel.State) {
        Log.d("Tunnel", "State changed to: $newState")
    }
}
