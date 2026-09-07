package br.gymcore.services;

import br.gymcore.dtos.AlunoTurmaListagemDto;
import br.gymcore.dtos.TurmaHorarioDto;
import br.gymcore.entities.AlunoTurma;
import br.gymcore.entities.Matricula;
import br.gymcore.entities.PlanoUnidadeModalidade;
import br.gymcore.entities.Turma;
import br.gymcore.entities.TurmaHorario;
import br.gymcore.enums.MatriculaStatus;
import br.gymcore.forms.AlunoTurmaForm;
import br.gymcore.repositories.AlunoTurmaRepository;
import br.gymcore.repositories.MatriculaRepository;
import br.gymcore.repositories.PlanoUnidadeModalidadeRepository;
import br.gymcore.repositories.TurmaHorarioRepository;
import br.gymcore.repositories.TurmaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlunoTurmaService {

    private final MatriculaRepository matriculaRepository;
    private final TurmaRepository turmaRepository;
    private final TurmaHorarioRepository turmaHorarioRepository;
    private final AlunoTurmaRepository alunoTurmaRepository;
    private final PlanoUnidadeModalidadeRepository planoUnidadeModalidadeRepository;

    @Transactional
    public Long matricular(AlunoTurmaForm form) {
        Matricula matricula = matriculaRepository.findById(form.getIdMatricula())
                .orElseThrow(() -> new EntityNotFoundException("Matrícula não encontrada"));

        if (matricula.getStatus() != MatriculaStatus.ATIVA) {
            throw new IllegalStateException("Somente matrículas ativas podem ser inscritas em turmas");
        }

        Turma turma = turmaRepository.findById(form.getIdTurma())
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada"));

        if (!Boolean.TRUE.equals(turma.getAtivo())) {
            throw new IllegalStateException("Turma inativa não aceita novas matrículas");
        }

        Long idUnidadeDaMatricula = matricula.getPlanoUnidade().getUnidade().getId();
        Long idUnidadeDaTurma = turma.getUnidadeModalidade().getUnidade().getId();

        if (!Objects.equals(idUnidadeDaMatricula, idUnidadeDaTurma)) {
            throw new IllegalArgumentException("A turma não pertence à unidade da matrícula");
        }

        validarModalidadeContemplada(matricula, turma);

        if (alunoTurmaRepository.existsByMatricula_IdAndTurma_IdAndAtivoTrue(matricula.getId(), turma.getId())) {
            throw new IllegalStateException("Aluno já está inscrito nesta turma");
        }

        long ocupadas = alunoTurmaRepository.countByTurma_IdAndAtivoTrue(turma.getId());

        if (turma.getCapacidade() != null && ocupadas >= turma.getCapacidade()) {
            throw new IllegalStateException("Turma está lotada");
        }

        AlunoTurma alunoTurma = new AlunoTurma();
        alunoTurma.setMatricula(matricula);
        alunoTurma.setTurma(turma);
        alunoTurma.setDataInicio(form.getDataInicio() != null ? form.getDataInicio() : LocalDate.now());
        alunoTurma.setAtivo(form.getAtivo() != null ? form.getAtivo() : Boolean.TRUE);

        return alunoTurmaRepository.save(alunoTurma).getId();
    }

    @Transactional(readOnly = true)
    public List<AlunoTurmaListagemDto> listarPorMatricula(Long idMatricula) {
        List<AlunoTurma> vinculos = alunoTurmaRepository.listarPorMatricula(idMatricula);

        if (vinculos.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> turmaIds = vinculos.stream().map(vinculo -> vinculo.getTurma().getId()).toList();
        Map<Long, List<TurmaHorario>> horariosPorTurma = turmaHorarioRepository
                .findAllByTurma_IdInOrderByDiaSemanaAscHoraInicioAsc(turmaIds)
                .stream()
                .collect(Collectors.groupingBy(horario -> horario.getTurma().getId()));

        return vinculos.stream()
                .map(vinculo -> toDto(vinculo, horariosPorTurma.getOrDefault(vinculo.getTurma().getId(), Collections.emptyList())))
                .toList();
    }

    private void validarModalidadeContemplada(Matricula matricula, Turma turma) {
        List<PlanoUnidadeModalidade> restricoes = planoUnidadeModalidadeRepository
                .findAllByPlanoUnidade_IdIn(List.of(matricula.getPlanoUnidade().getId()));

        if (restricoes.isEmpty()) {
            return;
        }

        boolean contemplada = restricoes.stream()
                .anyMatch(restricao -> Objects.equals(
                        restricao.getUnidadeModalidade().getId(),
                        turma.getUnidadeModalidade().getId()
                ));

        if (!contemplada) {
            throw new IllegalArgumentException("O plano da matrícula não contempla a modalidade desta turma");
        }
    }

    private AlunoTurmaListagemDto toDto(AlunoTurma alunoTurma, List<TurmaHorario> horarios) {
        Turma turma = alunoTurma.getTurma();

        return new AlunoTurmaListagemDto(
                String.valueOf(alunoTurma.getId()),
                String.valueOf(turma.getId()),
                turma.getNome(),
                turma.getUnidadeModalidade().getModalidade().getNome(),
                turma.getProfessorUnidade().getProfessor().getPessoa().getNome(),
                horarios.stream()
                        .map(horario -> new TurmaHorarioDto(horario.getDiaSemana(), horario.getHoraInicio(), horario.getHoraFim()))
                        .toList(),
                alunoTurma.getDataInicio(),
                alunoTurma.getDataFim(),
                alunoTurma.getAtivo()
        );
    }
}
