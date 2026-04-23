package com.geekay.vpnapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.net.VpnService
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.geekay.vpnapp.data.repository.AuthRepository
import com.geekay.vpnapp.ui.DashboardScreen
import com.geekay.vpnapp.ui.DashboardViewModel
import com.geekay.vpnapp.ui.LoginScreen
import com.geekay.vpnapp.ui.theme.VpnAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    private var pendingVpnConnect: (() -> Unit)? = null

    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            pendingVpnConnect?.invoke()
        }
        pendingVpnConnect = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VpnAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VpnApp(
                        initialLoginState = authRepository.isLoggedIn(),
                        onLogoutAction = { authRepository.logout() },
                        onRequestVpnPermission = { connectAction ->
                            val intent = VpnService.prepare(this)
                            if (intent != null) {
                                pendingVpnConnect = connectAction
                                vpnPermissionLauncher.launch(intent)
                            } else {
                                // Already has permission
                                connectAction()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun VpnApp(
    initialLoginState: Boolean,
    onLogoutAction: () -> Unit,
    onRequestVpnPermission: (connectAction: () -> Unit) -> Unit
) {
    var isLoggedIn by remember { mutableStateOf(initialLoginState) }
    val dashboardViewModel: DashboardViewModel = hiltViewModel()

    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = { isLoggedIn = true }
        )
    } else {
        DashboardScreen(
            viewModel = dashboardViewModel,
            onLogout = { 
                onLogoutAction()
                isLoggedIn = false 
            },
            onRequestVpnPermission = {
                onRequestVpnPermission {
                    dashboardViewModel.toggleVpn()
                }
            }
        )
    }
}
