package com.example.backend.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FcmService {

    /**
     * Envía una notificación push a un dispositivo específico usando su Token FCM.
     */
    public boolean enviarNotificacionAlerta(String targetFcmToken, String titulo, String mensaje, Long alertaId) {
        if (targetFcmToken == null || targetFcmToken.isBlank()) {
            System.err.println("⚠️ No se puede enviar notificación: El token FCM es nulo o vacío.");
            return false;
        }

        try {
            Message message = Message.builder()
                    .setToken(targetFcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(titulo)
                            .setBody(mensaje)
                            .build())
                    .putData("alertaId", String.valueOf(alertaId))
                    .putData("tipo", "EMERGENCIA")
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("🟢 Notificación enviada con éxito. ID de respuesta: " + response);
            return true;
        } catch (Exception e) {
            System.err.println("🔴 Error al enviar notificación FCM: " + e.getMessage());
            return false;
        }
    }
}