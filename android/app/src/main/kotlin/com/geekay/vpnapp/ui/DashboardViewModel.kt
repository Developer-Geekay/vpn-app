package com.geekay.vpnapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekay.vpnapp.vpn.VpnManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val vpnManager: VpnManager
) : ViewModel() {

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    fun toggleVpn() {
        viewModelScope.launch {
            if (_isConnected.value) {
                val success = vpnManager.disconnect()
                if (success) _isConnected.value = false
            } else {
                val success = vpnManager.connect()
                if (success) _isConnected.value = true
            }
        }
    }
}
