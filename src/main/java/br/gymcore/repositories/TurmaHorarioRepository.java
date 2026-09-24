package br.gymcore.repositories;

import br.gymcore.entities.TurmaHorario;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurmaHorarioRepository extends JpaRepository<TurmaHorario, Long> {

    List<TurmaHorario> findAllByTurma_IdInOrderByDiaSemanaAscHoraInicioAsc(Collection<Long> turmaIds);

    List<TurmaHorario> findAllByTurma_IdOrderByDiaSemanaAscHoraInicioAsc(Long idTurma);

    void deleteAllByTurma_Id(Long idTurma);
}
