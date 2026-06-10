package com.example.backend.util;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

public class AesUtil {

    private static final String ALGORITMO = "AES/GCM/NoPadding";
    private static final int TAMAÑO_IV = 12; 
    private static final int TAMAÑO_TAG = 128;

    // Método para Encriptar
    public static String encriptar(String textoPlano, String claveSecreta) throws Exception {
        byte[] iv = new byte[TAMAÑO_IV];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITMO);
        SecretKeySpec keySpec = new SecretKeySpec(claveSecreta.getBytes(), "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAMAÑO_TAG, iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
        byte[] textoCifrado = cipher.doFinal(textoPlano.getBytes());

        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + textoCifrado.length);
        byteBuffer.put(iv);
        byteBuffer.put(textoCifrado);

        return Base64.getEncoder().encodeToString(byteBuffer.array());
    }

    // Método para Desencriptar
    public static String desencriptar(String textoCifradoBase64, String claveSecreta) throws Exception {
        byte[] mensajeCifradoConIv = Base64.getDecoder().decode(textoCifradoBase64);

        ByteBuffer byteBuffer = ByteBuffer.wrap(mensajeCifradoConIv);
        byte[] iv = new byte[TAMAÑO_IV];
        byteBuffer.get(iv);

        byte[] textoCifrado = new byte[byteBuffer.remaining()];
        byteBuffer.get(textoCifrado);

        Cipher cipher = Cipher.getInstance(ALGORITMO);
        SecretKeySpec keySpec = new SecretKeySpec(claveSecreta.getBytes(), "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAMAÑO_TAG, iv);

        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
        byte[] textoPlano = cipher.doFinal(textoCifrado);

        return new String(textoPlano);
    }
}