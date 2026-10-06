package com.example.backend.service;

import com.example.backend.util.AesUtil;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
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
            // 1. Garantizamos que Firebase esté inicializado antes de obtener la instancia
            inicializarFirebaseSiEsNecesario();

            // 2. Formato forzado con Locale.US para asegurar el punto decimal (.) en las coordenadas
            String jsonPayload = String.format(
                Locale.US,
                "{\"id_alerta\":%d,\"id_usuario\":%d,\"nombre_victima\":\"%s\",\"mensaje\":\"%s\",\"latitud\":%f,\"longitud\":%f}",
                idAlerta, idUsuario, nombreVictima, mensaje, lat, lng
            );

            // 3. Cifrado mediante AesUtil
            String datosSegurosCifrados = AesUtil.encriptar(jsonPayload, LLAVE_SECRETA);

            Message message = Message.builder()
                    .setToken(targetFcmToken)
                    // Únicamente mandamos los datos seguros cifrados
                    .putData("datos_seguros", datosSegurosCifrados)
                    // Prioridad Alta para asegurar que Android despierte la app inmediatamente en segundo plano
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
}