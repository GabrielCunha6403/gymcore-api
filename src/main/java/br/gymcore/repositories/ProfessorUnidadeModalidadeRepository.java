package br.gymcore.repositories;

import br.gymcore.entities.ProfessorUnidadeModalidade;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorUnidadeModalidadeRepository extends JpaRepository<ProfessorUnidadeModalidade, Long> {

    List<ProfessorUnidadeModalidade> findAllByProfessorUnidade_IdIn(Collection<Long> professorUnidadeIds);
}
