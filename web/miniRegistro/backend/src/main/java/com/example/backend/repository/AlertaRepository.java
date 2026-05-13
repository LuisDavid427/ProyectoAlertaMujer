package com.example.backend.repository;

import com.example.backend.model.AlertaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<AlertaModel, Integer> {
    
    List<AlertaModel> findByEstadoAlerta(String estadoAlerta);

    @Query(value = "CALL sp_listar_alertas_dashboard(:busqueda)", nativeQuery = true)
    List<Object[]> llamarSpAlertas(String busqueda);

}