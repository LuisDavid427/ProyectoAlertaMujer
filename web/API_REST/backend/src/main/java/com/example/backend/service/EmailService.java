package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Async
    public void enviarCorreoCodigo(String paraDestino, String codigo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(paraDestino);
        mensaje.setSubject("Código de recuperación de contraseña - Alerta Mujer");
        mensaje.setText("Hola,\n\nHas solicitado restablecer tu contraseña para la aplicación Alerta Mujer. Tu código de verificación de 6 dígitos es:\n\n" 
                        + codigo + 
                        "\n\nEste código expirará en 15 minutos.\nSi no solicitaste este cambio, puedes ignorar este mensaje.");
        
        mailSender.send(mensaje);
    }
}