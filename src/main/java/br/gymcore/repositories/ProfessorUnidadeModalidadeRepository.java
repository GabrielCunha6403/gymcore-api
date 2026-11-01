package br.gymcore.repositories;

import br.gymcore.entities.ProfessorUnidadeModalidade;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfessorUnidadeModalidadeRepository extends JpaRepository<ProfessorUnidadeModalidade, Long> {

    List<ProfessorUnidadeModalidade> findAllByProfessorUnidade_IdIn(Collection<Long> professorUnidadeIds);

    void deleteAllByProfessorUnidade_Id(Long idProfessorUnidade);

    @Query("""
            select pum
            from ProfessorUnidadeModalidade pum
            join fetch pum.professorUnidade pu
            join fetch pu.professor p
            join fetch p.pessoa pe
            where pum.unidadeModalidade.id in :idsUnidadeModalidade
            """)
    List<ProfessorUnidadeModalidade> findAllByUnidadeModalidade_IdIn(
            @Param("idsUnidadeModalidade") Collection<Long> idsUnidadeModalidade
    );

    @Query("""
            select pum
            from ProfessorUnidadeModalidade pum
            join fetch pum.professorUnidade pu
            join fetch pu.professor p
            join fetch p.pessoa pe
            join fetch pu.unidade u
            where pum.unidadeModalidade.id in :idsUnidadeModalidade
              and u.estabelecimento.id = :idEstabelecimento
              and pum.ativo = true
            order by u.nome, pe.nome
            """)
    List<ProfessorUnidadeModalidade> listarAtivosPorUnidadeModalidade(
            @Param("idsUnidadeModalidade") Collection<Long> idsUnidadeModalidade,
            @Param("idEstabelecimento") Long idEstabelecimento
    );
}
