package com.example.registro.repository;

import com.example.registro.model.RolModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolRepository extends JpaRepository<RolModel, Integer> {
    Optional<RolModel> findByNombreRol(String nombreRol);
}