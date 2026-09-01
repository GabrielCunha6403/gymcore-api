package br.gymcore.repositories;

import br.gymcore.entities.ProfessorUnidade;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorUnidadeRepository extends JpaRepository<ProfessorUnidade, Long> {

    List<ProfessorUnidade> findAllByProfessor_IdIn(Collection<Long> professorIds);

    List<ProfessorUnidade> findAllByUnidade_Id(Long idUnidade);
}
