package com.example.backend.repository;

import com.example.backend.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Integer> {

    // Extrae el usuario para que Spring Boot valide el Hash
    Optional<UsuarioModel> findByEmail(String email);

    // Mantenemos el SP que no involucra contraseñas
    @Query(value = "CALL sp_listar_usuarios_dashboard(:busqueda)", nativeQuery = true)
    List<Object[]> llamarSpUsuarios(@Param("busqueda") String busqueda); 
}