package com.example.backend.service;

import com.example.backend.util.AesUtil;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Locale;

@Service
public class FcmService {

    private static final String LLAVE_SECRETA = "AlertaMujerSuperSecretKey2026!!!";

    // Método privado para asegurar que Firebase esté listo antes de usarlo
    private synchronized void inicializarFirebaseSiEsNecesario() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                ClassPathResource resource = new ClassPathResource("alerta-mujer-d404e-firebase-adminsdk-fbsvc-fcfa4eaebb.json");
                
                if (!resource.exists()) {
                    throw new RuntimeException("🔴 No se encontró el archivo JSON de Firebase en src/main/resources/");
                }

                try (InputStream serviceAccount = resource.getInputStream()) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();

                    FirebaseApp.initializeApp(options);
                    System.out.println("🟢 ¡Firebase Admin SDK inicializado desde FcmService con [DEFAULT]!");
                }
            }
        } catch (Exception e) {
            System.err.println("🔴 Error crítico inicializando Firebase en FcmService: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean enviarAlertaSegura(String targetFcmToken, Integer idAlerta, Integer idUsuario, String nombreVictima, String mensaje, Double lat, Double lng) {
        if (targetFcmToken == null || targetFcmToken.isBlank()) {
            System.err.println("⚠️ No se puede enviar notificación: El token FCM es nulo o vacío.");
            return false;
        }

        try {
            inicializarFirebaseSiEsNecesario();

            String jsonPayload = String.format(
                Locale.US,
                "{\"id_alerta\":%d,\"id_usuario\":%d,\"nombre_victima\":\"%s\",\"mensaje\":\"%s\",\"latitud\":%f,\"longitud\":%f}",
                idAlerta, idUsuario, nombreVictima, mensaje, lat, lng
            );

            String datosSegurosCifrados = AesUtil.encriptar(jsonPayload, LLAVE_SECRETA);

            Message message = Message.builder()
                    .setToken(targetFcmToken)
                    .putData("datos_seguros", datosSegurosCifrados)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .build();
                    
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("🟢 Notificación push de emergencia enviada con éxito. ID: " + response);
            return true;
        } catch (Exception e) {
            System.err.println("🔴 Error al enviar notificación FCM cifrada: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean enviarNotificacionEvidencia(String targetFcmToken, Integer idAlerta, String nombreVictima, String tipoEvidencia, String urlEvidencia) {
        if (targetFcmToken == null || targetFcmToken.isBlank()) {
            System.err.println("⚠️ No se puede enviar notificación de evidencia: El token FCM es nulo o vacío.");
            return false;
        }

        try {
            inicializarFirebaseSiEsNecesario();

            String jsonPayload = String.format(
                Locale.US,
                "{\"id_alerta\":%d,\"nombre_victima\":\"%s\",\"tipo_evidencia\":\"%s\",\"url_evidencia\":\"%s\",\"accion\":\"nueva_evidencia\"}",
                idAlerta, nombreVictima, tipoEvidencia, urlEvidencia
            );

            String datosSegurosCifrados = AesUtil.encriptar(jsonPayload, LLAVE_SECRETA);

            Message message = Message.builder()
                    .setToken(targetFcmToken)
                    .putData("datos_seguros", datosSegurosCifrados)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("🟢 Notificación push de evidencia enviada con éxito. ID: " + response);
            return true;
        } catch (Exception e) {
            System.err.println("🔴 Error al enviar notificación FCM cifrada de evidencia: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // NUEVO: Método para notificar cambios de estado (ej. alerta_desactivada, alerta_activada)
    public boolean enviarNotificacionEstado(String targetFcmToken, Integer idAlerta, String nombreVictima, String accion) {
        if (targetFcmToken == null || targetFcmToken.isBlank()) {
            System.err.println("⚠️ No se puede enviar notificación de estado: El token FCM es nulo o vacío.");
            return false;
        }

        try {
            inicializarFirebaseSiEsNecesario();

            String jsonPayload = String.format(
                Locale.US,
                "{\"id_alerta\":%d,\"nombre_victima\":\"%s\",\"accion\":\"%s\"}",
                idAlerta, nombreVictima, accion
            );

            String datosSegurosCifrados = AesUtil.encriptar(jsonPayload, LLAVE_SECRETA);

            Message message = Message.builder()
                    .setToken(targetFcmToken)
                    .putData("datos_seguros", datosSegurosCifrados)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("🟢 Notificación push de estado (" + accion + ") enviada con éxito. ID: " + response);
            return true;
        } catch (Exception e) {
            System.err.println("🔴 Error al enviar notificación FCM de estado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}