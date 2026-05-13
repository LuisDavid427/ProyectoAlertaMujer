package com.example.backend.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.model.UsuarioContactoModel;
import java.util.List;

public interface UsuarioContactoRepository extends JpaRepository<UsuarioContactoModel, Integer> {

    // Llamando a tu Procedimiento Almacenado nativo
    @Query(value = "CALL sp_obtener_usuario_contactos(:p_id_usuario)", nativeQuery = true)
    List<Integer> obtenerIdsContactos(@Param("p_id_usuario") Integer idUsuario);
    
}