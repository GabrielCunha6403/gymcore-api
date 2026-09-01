package br.gymcore.repositories;

import br.gymcore.entities.Frequencia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FrequenciaRepository extends JpaRepository<Frequencia, Long> {

    List<Frequencia> findTop10ByAluno_IdOrderByDataHoraEntradaDesc(Long idAluno);
}
