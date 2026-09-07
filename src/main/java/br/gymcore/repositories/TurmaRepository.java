package br.gymcore.repositories;

import br.gymcore.entities.Turma;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TurmaRepository extends JpaRepository<Turma, Long> {

    @Query("""
            select t
            from Turma t
            join fetch t.unidadeModalidade um
            join fetch um.modalidade m
            join fetch t.professorUnidade pu
            join fetch pu.professor p
            join fetch p.pessoa pessoa
            where um.unidade.id = :idUnidade
            order by t.nome
            """)
    List<Turma> listarPorUnidade(@Param("idUnidade") Long idUnidade);

    @Query("""
            select t
            from Turma t
            join fetch t.unidadeModalidade um
            join fetch um.modalidade m
            join fetch t.professorUnidade pu
            join fetch pu.professor p
            join fetch p.pessoa pessoa
            where t.id = :idTurma
            """)
    Optional<Turma> buscarComDetalhes(@Param("idTurma") Long idTurma);

    @Query("""
            select t
            from Turma t
            join fetch t.unidadeModalidade um
            join fetch um.modalidade m
            join fetch t.professorUnidade pu
            join fetch pu.professor p
            join fetch p.pessoa pessoa
            where um.unidade.id = :idUnidade
              and t.ativo = true
            order by t.nome
            """)
    List<Turma> listarAtivasPorUnidade(@Param("idUnidade") Long idUnidade);
}
