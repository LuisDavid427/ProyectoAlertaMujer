package com.example.backend.repository;

import com.example.backend.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Integer> {

    // Extrae el usuario para que Spring Boot valide el Hash
    Optional<UsuarioModel> findByEmail(String email);

    // Mantenemos el SP que no involucra contraseñas
    @Query(value = "CALL sp_listar_usuarios_dashboard(:busqueda)", nativeQuery = true)
    List<Object[]> llamarSpUsuarios(@Param("busqueda") String busqueda);

    @Transactional
    @Modifying
    @Query("UPDATE UsuarioModel u SET u.fcmToken = :fcmToken WHERE u.id = :idUsuario")
    int actualizarFcmToken(@Param("idUsuario") Integer idUsuario, @Param("fcmToken") String fcmToken);
}