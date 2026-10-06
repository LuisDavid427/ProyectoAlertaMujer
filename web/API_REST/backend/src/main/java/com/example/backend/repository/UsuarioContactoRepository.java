package com.example.backend.repository;

import com.example.backend.model.UsuarioContactoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioContactoRepository extends JpaRepository<UsuarioContactoModel, Integer> {

        
    @Query(value = "CALL sp_obtener_usuario_contactos(:p_id_usuario)", nativeQuery = true)
    List<Object[]> obtenerContactos(@Param("p_id_usuario") Integer idUsuario);
}