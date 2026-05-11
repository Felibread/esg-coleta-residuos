package com.esg.coleta.repository;

import com.esg.coleta.model.Coleta;
import com.esg.coleta.model.StatusColeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColetaRepository extends JpaRepository<Coleta, Long> {

    List<Coleta> findByPontoColetaId(Long pontoColetaId);

    List<Coleta> findByStatus(StatusColeta status);

    long countByStatus(StatusColeta status);

    @Query("SELECT COALESCE(SUM(c.pesoColetadoKg), 0) FROM Coleta c WHERE c.status = 'CONCLUIDA'")
    Double sumPesoColetadoKgConcluidas();

    @Query("SELECT COALESCE(AVG(c.pesoColetadoKg), 0) FROM Coleta c WHERE c.status = 'CONCLUIDA'")
    Optional<Double> avgPesoColetadoKg();
}
