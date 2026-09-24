package br.gymcore.services;

import br.gymcore.dtos.TurmaAlunoListagemDto;
import br.gymcore.dtos.TurmaHorarioDto;
import br.gymcore.dtos.TurmaListagemDto;
import br.gymcore.entities.AlunoTurma;
import br.gymcore.entities.Matricula;
import br.gymcore.entities.PlanoUnidadeModalidade;
import br.gymcore.entities.ProfessorUnidade;
import br.gymcore.entities.Turma;
import br.gymcore.entities.TurmaHorario;
import br.gymcore.entities.UnidadeModalidade;
import br.gymcore.forms.TurmaForm;
import br.gymcore.repositories.AlunoTurmaRepository;
import br.gymcore.repositories.MatriculaRepository;
import br.gymcore.repositories.PlanoUnidadeModalidadeRepository;
import br.gymcore.repositories.ProfessorUnidadeRepository;
import br.gymcore.repositories.TurmaHorarioRepository;
import br.gymcore.repositories.TurmaRepository;
import br.gymcore.repositories.UnidadeModalidadeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final UnidadeModalidadeRepository unidadeModalidadeRepository;
    private final ProfessorUnidadeRepository professorUnidadeRepository;
    private final TurmaRepository turmaRepository;
    private final TurmaHorarioRepository turmaHorarioRepository;
    private final AlunoTurmaRepository alunoTurmaRepository;
    private final MatriculaRepository matriculaRepository;
    private final PlanoUnidadeModalidadeRepository planoUnidadeModalidadeRepository;

    @Transactional
    public Long cadastrar(TurmaForm form) {
        UnidadeModalidade unidadeModalidade = unidadeModalidadeRepository.findById(form.getIdUnidadeModalidade())
                .orElseThrow(() -> new EntityNotFoundException("Modalidade da unidade não encontrada"));

        ProfessorUnidade professorUnidade = professorUnidadeRepository
                .findByProfessor_IdAndUnidade_Id(form.getIdProfessor(), unidadeModalidade.getUnidade().getId())
                .orElseThrow(() -> new EntityNotFoundException("Professor não vinculado a esta unidade"));

        if (professorUnidade.getDataDesligamento() != null) {
            throw new IllegalStateException("Professor desligado desta unidade não pode ser vinculado a novas turmas");
        }

        Turma turma = new Turma();
        turma.setUnidadeModalidade(unidadeModalidade);
        turma.setProfessorUnidade(professorUnidade);
        turma.setNome(form.getNome());
        turma.setCapacidade(form.getCapacidade());
        turma.setAtivo(form.getAtivo() != null ? form.getAtivo() : Boolean.TRUE);

        turma = turmaRepository.save(turma);

        turmaHorarioRepository.saveAll(criarHorarios(form, turma));

        return turma.getId();
    }

    @Transactional
    public void atualizar(Long idTurma, TurmaForm form) {
        Turma turma = turmaRepository.findById(idTurma)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada"));

        UnidadeModalidade unidadeModalidade = unidadeModalidadeRepository.findById(form.getIdUnidadeModalidade())
                .orElseThrow(() -> new EntityNotFoundException("Modalidade da unidade não encontrada"));

        ProfessorUnidade professorUnidade = professorUnidadeRepository
                .findByProfessor_IdAndUnidade_Id(form.getIdProfessor(), unidadeModalidade.getUnidade().getId())
                .orElseThrow(() -> new EntityNotFoundException("Professor não vinculado a esta unidade"));

        if (professorUnidade.getDataDesligamento() != null) {
            throw new IllegalStateException("Professor desligado desta unidade não pode ser vinculado a novas turmas");
        }

        turma.setUnidadeModalidade(unidadeModalidade);
        turma.setProfessorUnidade(professorUnidade);
        turma.setNome(form.getNome());
        turma.setCapacidade(form.getCapacidade());
        turma.setAtivo(form.getAtivo() != null ? form.getAtivo() : Boolean.TRUE);

        turmaHorarioRepository.deleteAllByTurma_Id(turma.getId());
        turmaHorarioRepository.flush();
        turmaHorarioRepository.saveAll(criarHorarios(form, turma));
    }

    @Transactional(readOnly = true)
    public List<TurmaListagemDto> listarPorUnidade(Long idUnidade) {
        return montarListagem(turmaRepository.listarPorUnidade(idUnidade));
    }

    @Transactional(readOnly = true)
    public TurmaListagemDto getTurmaById(Long idTurma) {
        Turma turma = turmaRepository.buscarComDetalhes(idTurma)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada"));

        List<TurmaHorario> horarios = turmaHorarioRepository.findAllByTurma_IdOrderByDiaSemanaAscHoraInicioAsc(idTurma);
        long matriculados = alunoTurmaRepository.countByTurma_IdAndAtivoTrue(idTurma);

        return toDto(turma, horarios, matriculados);
    }

    @Transactional(readOnly = true)
    public List<TurmaListagemDto> listarDisponiveisParaMatricula(Long idMatricula) {
        Matricula matricula = matriculaRepository.findById(idMatricula)
                .orElseThrow(() -> new EntityNotFoundException("Matrícula não encontrada"));

        Long idUnidade = matricula.getPlanoUnidade().getUnidade().getId();
        List<Long> idsModalidadesPermitidas = modalidadesPermitidas(matricula);

        List<Turma> compativeis = turmaRepository.listarAtivasPorUnidade(idUnidade).stream()
                .filter(turma -> idsModalidadesPermitidas == null
                        || idsModalidadesPermitidas.contains(turma.getUnidadeModalidade().getId()))
                .filter(turma -> !alunoTurmaRepository.existsByMatricula_IdAndTurma_IdAndAtivoTrue(idMatricula, turma.getId()))
                .filter(turma -> turma.getCapacidade() == null
                        || alunoTurmaRepository.countByTurma_IdAndAtivoTrue(turma.getId()) < turma.getCapacidade())
                .toList();

        return montarListagem(compativeis);
    }

    @Transactional(readOnly = true)
    public List<TurmaAlunoListagemDto> listarAlunos(Long idTurma) {
        return alunoTurmaRepository.listarPorTurma(idTurma).stream()
                .map(this::toAlunoDto)
                .toList();
    }

    private List<Long> modalidadesPermitidas(Matricula matricula) {
        List<PlanoUnidadeModalidade> restricoes = planoUnidadeModalidadeRepository
                .findAllByPlanoUnidade_IdIn(List.of(matricula.getPlanoUnidade().getId()));

        if (restricoes.isEmpty()) {
            return null;
        }

        return restricoes.stream()
                .map(restricao -> restricao.getUnidadeModalidade().getId())
                .toList();
    }

    private List<TurmaHorario> criarHorarios(TurmaForm form, Turma turma) {
        return form.getHorarios().stream()
                .map(horarioForm -> {
                    TurmaHorario horario = new TurmaHorario();
                    horario.setTurma(turma);
                    horario.setDiaSemana(horarioForm.getDiaSemana());
                    horario.setHoraInicio(horarioForm.getHoraInicio());
                    horario.setHoraFim(horarioForm.getHoraFim());
                    return horario;
                })
                .toList();
    }

    private List<TurmaListagemDto> montarListagem(List<Turma> turmas) {
        if (turmas.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> turmaIds = turmas.stream().map(Turma::getId).toList();

        Map<Long, List<TurmaHorario>> horariosPorTurma = turmaHorarioRepository
                .findAllByTurma_IdInOrderByDiaSemanaAscHoraInicioAsc(turmaIds)
                .stream()
                .collect(Collectors.groupingBy(horario -> horario.getTurma().getId()));

        Map<Long, Long> matriculadosPorTurma = alunoTurmaRepository.contarMatriculadosPorTurma(turmaIds).stream()
                .collect(Collectors.toMap(
                        AlunoTurmaRepository.MatriculadosPorTurma::getTurmaId,
                        AlunoTurmaRepository.MatriculadosPorTurma::getTotal
                ));

        return turmas.stream()
                .map(turma -> toDto(
                        turma,
                        horariosPorTurma.getOrDefault(turma.getId(), Collections.emptyList()),
                        matriculadosPorTurma.getOrDefault(turma.getId(), 0L)
                ))
                .toList();
    }

    private TurmaListagemDto toDto(Turma turma, List<TurmaHorario> horarios, long matriculados) {
        UnidadeModalidade unidadeModalidade = turma.getUnidadeModalidade();
        ProfessorUnidade professorUnidade = turma.getProfessorUnidade();

        return new TurmaListagemDto(
                String.valueOf(turma.getId()),
                String.valueOf(unidadeModalidade.getUnidade().getId()),
                String.valueOf(unidadeModalidade.getId()),
                unidadeModalidade.getModalidade().getNome(),
                String.valueOf(professorUnidade.getProfessor().getId()),
                professorUnidade.getProfessor().getPessoa().getNome(),
                turma.getNome(),
                turma.getCapacidade(),
                (int) matriculados,
                turma.getAtivo(),
                horarios.stream()
                        .map(horario -> new TurmaHorarioDto(horario.getDiaSemana(), horario.getHoraInicio(), horario.getHoraFim()))
                        .toList()
        );
    }

    private TurmaAlunoListagemDto toAlunoDto(AlunoTurma alunoTurma) {
        var aluno = alunoTurma.getMatricula().getAluno();

        return new TurmaAlunoListagemDto(
                String.valueOf(alunoTurma.getId()),
                String.valueOf(aluno.getId()),
                aluno.getPessoa().getNome(),
                alunoTurma.getDataInicio(),
                alunoTurma.getDataFim(),
                alunoTurma.getAtivo()
        );
    }
}
