package com.esg.coleta.repository;

import com.esg.coleta.model.PontoColeta;
import com.esg.coleta.model.TipoResiduo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PontoColetaRepository extends JpaRepository<PontoColeta, Long> {

    List<PontoColeta> findByAtivo(Boolean ativo);

    List<PontoColeta> findByTipoResiduo(TipoResiduo tipoResiduo);

    long countByAtivo(Boolean ativo);
}
