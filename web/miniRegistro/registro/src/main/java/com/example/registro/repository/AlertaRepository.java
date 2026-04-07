package com.example.registro.repository;

import com.example.registro.model.AlertaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<AlertaModel, Integer> {
    
    List<AlertaModel> findByEstadoAlerta(String estadoAlerta);
}