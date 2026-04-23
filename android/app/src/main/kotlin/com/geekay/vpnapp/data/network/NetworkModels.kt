package com.geekay.vpnapp.data.network

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String
)

data class VpnConfigResponse(
    @SerializedName("interface_private_key") val interfacePrivateKey: String,
    @SerializedName("interface_address") val interfaceAddress: String,
    @SerializedName("peer_public_key") val peerPublicKey: String,
    @SerializedName("peer_endpoint") val peerEndpoint: String,
    @SerializedName("peer_allowed_ips") val peerAllowedIps: String
)
