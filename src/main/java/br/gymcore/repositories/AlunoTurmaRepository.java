package br.gymcore.repositories;

import br.gymcore.entities.AlunoTurma;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlunoTurmaRepository extends JpaRepository<AlunoTurma, Long> {

    boolean existsByMatricula_IdAndTurma_IdAndAtivoTrue(Long idMatricula, Long idTurma);

    long countByTurma_IdAndAtivoTrue(Long idTurma);

    @Query("""
            select at
            from AlunoTurma at
            join fetch at.matricula m
            join fetch m.aluno a
            join fetch a.pessoa p
            where at.turma.id = :idTurma
            order by p.nome
            """)
    List<AlunoTurma> listarPorTurma(@Param("idTurma") Long idTurma);

    @Query("""
            select at
            from AlunoTurma at
            join fetch at.turma t
            join fetch t.unidadeModalidade um
            join fetch um.modalidade mod
            join fetch t.professorUnidade pu
            join fetch pu.professor p
            join fetch p.pessoa pessoa
            where at.matricula.id = :idMatricula
            order by t.nome
            """)
    List<AlunoTurma> listarPorMatricula(@Param("idMatricula") Long idMatricula);

    @Query("""
            select at.turma.id as turmaId, count(at) as total
            from AlunoTurma at
            where at.turma.id in :turmaIds
              and at.ativo = true
            group by at.turma.id
            """)
    List<MatriculadosPorTurma> contarMatriculadosPorTurma(@Param("turmaIds") Collection<Long> turmaIds);

    interface MatriculadosPorTurma {
        Long getTurmaId();
        Long getTotal();
    }
}
