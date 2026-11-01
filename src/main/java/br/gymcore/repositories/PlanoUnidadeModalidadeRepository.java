package br.gymcore.repositories;

import br.gymcore.entities.PlanoUnidadeModalidade;
import br.gymcore.entities.PlanoUnidadeModalidadeId;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlanoUnidadeModalidadeRepository extends JpaRepository<PlanoUnidadeModalidade, PlanoUnidadeModalidadeId> {

    List<PlanoUnidadeModalidade> findAllByPlanoUnidade_IdIn(Collection<Long> planoUnidadeIds);

    void deleteAllByPlanoUnidade_Id(Long idPlanoUnidade);

    @Query("""
            select pum
            from PlanoUnidadeModalidade pum
            join fetch pum.planoUnidade pu
            join fetch pu.unidade u
            where pum.unidadeModalidade.id in :idsUnidadeModalidade
              and u.estabelecimento.id = :idEstabelecimento
              and pu.ativo = true
            order by u.nome, pu.nomeExibicao
            """)
    List<PlanoUnidadeModalidade> listarComPlanoUnidadeAtivo(
            @Param("idsUnidadeModalidade") Collection<Long> idsUnidadeModalidade,
            @Param("idEstabelecimento") Long idEstabelecimento
    );
}
