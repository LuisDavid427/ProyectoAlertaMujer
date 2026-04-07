package com.example.registro.repository;

import com.example.registro.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    @Query(value = "CALL sp_validar_login_admin(:p_email, :p_pass)", nativeQuery = true)
    Optional<Object[]> validarAccesoAdmin(@Param("p_email") String email, @Param("p_pass") String password);

    @Query(value = "CALL sp_validar_login_usuario(:email, :pass)", nativeQuery = true)
    List<Object[]> validarLoginUsuario(@Param("email") String email, @Param("pass") String pass);
}