package com.example.alertamujer.util

import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object AesUtil {

    private const val ALGORITMO = "AES/GCM/NoPadding"
    private const val TAMAÑO_IV = 12
    private const val TAMAÑO_TAG = 128

    // Método para desencriptar (el que usarás en tu AlertaFCMService)
    fun desencriptar(textoCifradoBase64: String, claveSecreta: String): String {
        val mensajeCifradoConIv = Base64.getDecoder().decode(textoCifradoBase64)

        val byteBuffer = ByteBuffer.wrap(mensajeCifradoConIv)
        val iv = ByteArray(TAMAÑO_IV)
        byteBuffer.get(iv)

        val textoCifrado = ByteArray(byteBuffer.remaining())
        byteBuffer.get(textoCifrado)

        val cipher = Cipher.getInstance(ALGORITMO)
        val keySpec = SecretKeySpec(claveSecreta.toByteArray(), "AES")
        val gcmSpec = GCMParameterSpec(TAMAÑO_TAG, iv)

        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)
        val textoPlano = cipher.doFinal(textoCifrado)

        return String(textoPlano)
    }
}