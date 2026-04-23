package com.geekay.vpnapp.data.repository

import com.geekay.vpnapp.data.local.TokenManager
import com.geekay.vpnapp.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun login(username: String, password: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(username, password)
            if (response.isSuccessful) {
                val token = response.body()?.accessToken
                if (token != null) {
                    tokenManager.saveToken(token)
                    return@withContext true
                }
            }
            return@withContext false
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }
}
