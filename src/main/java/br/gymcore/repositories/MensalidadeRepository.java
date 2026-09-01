package br.gymcore.repositories;

import br.gymcore.entities.Mensalidade;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensalidadeRepository extends JpaRepository<Mensalidade, Long> {

    List<Mensalidade> findAllByMatricula_Aluno_IdOrderByCompetenciaDesc(Long idAluno);
}
