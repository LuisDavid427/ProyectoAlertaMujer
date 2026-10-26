package com.example.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Apuntamos directamente a la ruta absoluta de Windows donde guardas las evidencias
        String rutaAbsoluta = Paths.get("C:/AlertaMujer/evidencias").toUri().toString();

        registry.addResourceHandler("/archivos/**")
                .addResourceLocations(rutaAbsoluta);
    }
}