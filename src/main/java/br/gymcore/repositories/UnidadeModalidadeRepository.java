package br.gymcore.repositories;

import br.gymcore.entities.UnidadeModalidade;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UnidadeModalidadeRepository extends JpaRepository<UnidadeModalidade, Long> {

    List<UnidadeModalidade> findAllByIdInAndUnidade_Id(Collection<Long> ids, Long unidadeId);

    boolean existsByUnidade_IdAndModalidade_Id(Long unidadeId, Long modalidadeId);

    @Query("""
            select um
            from UnidadeModalidade um
            join fetch um.modalidade m
            where um.unidade.id = :idUnidade
            order by m.nome
            """)
    List<UnidadeModalidade> listarPorUnidade(@Param("idUnidade") Long idUnidade);

    @Query("""
            select um
            from UnidadeModalidade um
            join fetch um.unidade u
            where um.modalidade.id in :idsModalidade
            """)
    List<UnidadeModalidade> findAllByModalidade_IdIn(@Param("idsModalidade") Collection<Long> idsModalidade);
}
