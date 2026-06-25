package com.example.alertamujer.data.dto

import com.google.gson.annotations.SerializedName

data class FcmTokenRequest(
    @SerializedName("idUsuario")
    val idUsuario: Int,
    val token: String
)