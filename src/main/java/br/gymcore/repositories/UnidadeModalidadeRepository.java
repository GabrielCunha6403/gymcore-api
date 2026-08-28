package br.gymcore.repositories;

import br.gymcore.entities.UnidadeModalidade;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnidadeModalidadeRepository extends JpaRepository<UnidadeModalidade, Long> {

    List<UnidadeModalidade> findAllByIdInAndUnidade_Id(Collection<Long> ids, Long unidadeId);
}
