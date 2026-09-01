package br.gymcore.services;

import br.gymcore.dtos.UnidadeHorarioFuncionamentoListagemDto;
import br.gymcore.entities.Unidade;
import br.gymcore.entities.UnidadeHorarioFuncionamento;
import br.gymcore.forms.UnidadeHorarioFuncionamentoForm;
import br.gymcore.repositories.UnidadeHorarioFuncionamentoRepository;
import br.gymcore.repositories.UnidadeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnidadeHorarioFuncionamentoService {

    private final UnidadeRepository unidadeRepository;
    private final UnidadeHorarioFuncionamentoRepository unidadeHorarioFuncionamentoRepository;

    @Transactional(readOnly = true)
    public List<UnidadeHorarioFuncionamentoListagemDto> listar(Long idUnidade) {
        return unidadeHorarioFuncionamentoRepository.findAllByUnidade_IdOrderByDiaSemanaAsc(idUnidade)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void salvar(UnidadeHorarioFuncionamentoForm form) {
        Unidade unidade = unidadeRepository.findById(form.getIdUnidade())
                .orElseThrow(() -> new EntityNotFoundException("Unidade não encontrada"));

        form.getHorarios().forEach(this::validarHorario);

        unidadeHorarioFuncionamentoRepository.deleteAllByUnidade_Id(unidade.getId());

        List<UnidadeHorarioFuncionamento> horarios = form.getHorarios().stream()
                .map(horario -> criarHorario(unidade, horario))
                .toList();

        unidadeHorarioFuncionamentoRepository.saveAll(horarios);
    }

    private void validarHorario(UnidadeHorarioFuncionamentoForm.Horario horario) {
        LocalTime abertura = horario.getHoraAbertura();
        LocalTime fechamento = horario.getHoraFechamento();

        if ((abertura == null) != (fechamento == null)) {
            throw new IllegalArgumentException(
                    "Informe hora de abertura e fechamento juntas, ou deixe ambas em branco para indicar dia fechado"
            );
        }

        if (abertura != null && !abertura.isBefore(fechamento)) {
            throw new IllegalArgumentException("Hora de abertura deve ser anterior à hora de fechamento");
        }
    }

    private UnidadeHorarioFuncionamento criarHorario(Unidade unidade, UnidadeHorarioFuncionamentoForm.Horario item) {
        UnidadeHorarioFuncionamento horario = new UnidadeHorarioFuncionamento();
        horario.setUnidade(unidade);
        horario.setDiaSemana(item.getDiaSemana());
        horario.setHoraAbertura(item.getHoraAbertura());
        horario.setHoraFechamento(item.getHoraFechamento());
        return horario;
    }

    private UnidadeHorarioFuncionamentoListagemDto toDto(UnidadeHorarioFuncionamento horario) {
        return new UnidadeHorarioFuncionamentoListagemDto(
                horario.getDiaSemana(),
                horario.getHoraAbertura(),
                horario.getHoraFechamento()
        );
    }
}
