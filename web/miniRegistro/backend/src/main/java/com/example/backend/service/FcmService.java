package com.example.backend.service;

import com.example.backend.util.AesUtil; 
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FcmService {

    // RECUERDA: Esta llave debe tener exactamente 32 caracteres y ser idéntica a la de Android
    private static final String LLAVE_SECRETA = "AlertaMujerSuperSecretKey2026!!!";

    public void enviarAlertaAProtectores(List<String> tokens, String nombreVictima, String mensaje) {
        if (tokens.isEmpty()) return;

        try {
            // 1. Empaquetamos las variables de forma segura
            Map<String, String> datos = new HashMap<>();
            datos.put("nombre_victima", nombreVictima);
            datos.put("mensaje", mensaje);
            
            // Convertimos el mapa a un texto JSON: {"nombre_victima":"Ana","mensaje":"Ayuda"}
            String jsonPayload = new ObjectMapper().writeValueAsString(datos);

            // 2. ENCRIPTAMOS EL JSON (Cerramos el cofre)
            String payloadEncriptado = AesUtil.encriptar(jsonPayload, LLAVE_SECRETA);

            // 3. Construir el mensaje para múltiples dispositivos enviando SOLO el texto encriptado
            MulticastMessage message = MulticastMessage.builder()
                .putData("datos_seguros", payloadEncriptado) // Mandamos todo bajo una sola llave
                .setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH) // Prioridad alta para despertar el móvil
                    .build())
                .addAllTokens(tokens)
                .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            System.out.println("Alertas seguras enviadas con éxito: " + response.getSuccessCount());

        } catch (Exception e) {
            System.out.println("Error grave de seguridad al procesar la alerta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}