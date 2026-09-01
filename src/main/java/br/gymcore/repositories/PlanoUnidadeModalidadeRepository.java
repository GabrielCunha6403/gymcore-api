package br.gymcore.repositories;

import br.gymcore.entities.PlanoUnidadeModalidade;
import br.gymcore.entities.PlanoUnidadeModalidadeId;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanoUnidadeModalidadeRepository extends JpaRepository<PlanoUnidadeModalidade, PlanoUnidadeModalidadeId> {

    List<PlanoUnidadeModalidade> findAllByPlanoUnidade_IdIn(Collection<Long> planoUnidadeIds);
}
