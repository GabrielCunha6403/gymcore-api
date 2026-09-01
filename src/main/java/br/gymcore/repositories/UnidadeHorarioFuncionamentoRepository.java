package br.gymcore.repositories;

import br.gymcore.entities.UnidadeHorarioFuncionamento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnidadeHorarioFuncionamentoRepository extends JpaRepository<UnidadeHorarioFuncionamento, Long> {

    List<UnidadeHorarioFuncionamento> findAllByUnidade_IdOrderByDiaSemanaAsc(Long idUnidade);

    void deleteAllByUnidade_Id(Long idUnidade);
}
