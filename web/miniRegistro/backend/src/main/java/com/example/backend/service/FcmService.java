package com.example.backend.service;


import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FcmService {

    public void enviarAlertaAProtectores(List<String> tokens, String nombreVictima, String mensaje) {
        if (tokens.isEmpty()) return;

        // Construir el mensaje para múltiples dispositivos
        MulticastMessage message = MulticastMessage.builder()
            .putData("nombre_victima", nombreVictima)
            .putData("mensaje", mensaje)
            .setAndroidConfig(AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH) // Prioridad alta para despertar el móvil
                .build())
            .addAllTokens(tokens)
            .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            System.out.println("Alertas enviadas con éxito: " + response.getSuccessCount());
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }
}