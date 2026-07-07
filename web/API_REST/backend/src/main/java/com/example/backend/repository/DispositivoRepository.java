package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.model.DispositivoModel; // Ajustado a tu paquete de modelos
import java.util.Optional;

public interface DispositivoRepository extends JpaRepository<DispositivoModel, Integer> {
    
    // Busca un dispositivo específico por su token de Firebase
    Optional<DispositivoModel> findByTokenFcm(String token);
}