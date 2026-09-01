package br.gymcore.repositories;

import br.gymcore.entities.Matricula;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    List<Matricula> findAllByAluno_IdIn(Collection<Long> alunoIds);

    List<Matricula> findAllByAluno_Id(Long alunoId);
}
