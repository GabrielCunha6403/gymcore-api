package br.gymcore.services;

import br.gymcore.dtos.AlunoDetalheDto;
import br.gymcore.dtos.AlunoListagemDto;
import br.gymcore.dtos.PageDto;
import br.gymcore.entities.Aluno;
import br.gymcore.entities.Matricula;
import br.gymcore.entities.Modalidade;
import br.gymcore.entities.Pessoa;
import br.gymcore.entities.Plano;
import br.gymcore.entities.PlanoUnidade;
import br.gymcore.entities.PlanoUnidadeModalidade;
import br.gymcore.entities.Unidade;
import br.gymcore.entities.UnidadeModalidade;
import br.gymcore.forms.AlunoForm;
import br.gymcore.repositories.AlunoRepository;
import br.gymcore.repositories.MatriculaRepository;
import br.gymcore.repositories.PessoaRepository;
import br.gymcore.repositories.PlanoUnidadeModalidadeRepository;
import br.gymcore.repositories.PlanoUnidadeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final PessoaRepository pessoaRepository;
    private final AlunoRepository alunoRepository;
    private final MatriculaRepository matriculaRepository;
    private final PlanoUnidadeRepository planoUnidadeRepository;
    private final PlanoUnidadeModalidadeRepository planoUnidadeModalidadeRepository;

    @Transactional
    public Long cadastrar(AlunoForm form) {
        PlanoUnidade planoUnidade = planoUnidadeRepository.findById(form.getMatricula().getPlanoUnidadeId())
                .orElseThrow(() -> new EntityNotFoundException("Plano da unidade não encontrado"));

        Pessoa pessoa = pessoaRepository.save(criarPessoa(form));
        Aluno aluno = alunoRepository.save(criarAluno(form, pessoa));
        matriculaRepository.save(criarMatricula(form, aluno, planoUnidade));

        return aluno.getId();
    }

    @Transactional(readOnly = true)
    public PageDto<AlunoListagemDto> listar(String busca, Pageable pageable) {
        String buscaNormalizada = normalizarTexto(busca);
        Pageable pageableOrdenado = ajustarOrdenacao(pageable);

        Page<Aluno> alunos = buscaNormalizada == null
                ? alunoRepository.findAll(pageableOrdenado)
                : alunoRepository.listarComFiltros(buscaNormalizada, pageableOrdenado);

        Map<Long, List<Matricula>> matriculasPorAluno = buscarMatriculasPorAluno(alunos.getContent());
        Map<Long, List<PlanoUnidadeModalidade>> modalidadesPorPlanoUnidade =
                buscarModalidadesPorPlanoUnidade(matriculasPorAluno.values());

        Page<AlunoListagemDto> alunosDto = alunos.map(aluno -> toDto(
                aluno,
                matriculasPorAluno.getOrDefault(aluno.getId(), Collections.emptyList()),
                modalidadesPorPlanoUnidade
        ));

        return PageDto.from(alunosDto);
    }

    @Transactional(readOnly = true)
    public List<AlunoListagemDto> listarPorUnidade(Long idUnidade) {
        List<Matricula> matriculas = matriculaRepository.findAllByPlanoUnidade_Unidade_Id(idUnidade);

        if (matriculas.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<Matricula>> matriculasPorAluno = matriculas.stream()
                .collect(Collectors.groupingBy(matricula -> matricula.getAluno().getId()));
        Map<Long, List<PlanoUnidadeModalidade>> modalidadesPorPlanoUnidade =
                buscarModalidadesPorPlanoUnidade(matriculasPorAluno.values());

        return matriculasPorAluno.values().stream()
                .map(matriculasDoAluno -> toDto(
                        matriculasDoAluno.get(0).getAluno(),
                        matriculasDoAluno,
                        modalidadesPorPlanoUnidade
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public AlunoDetalheDto getAlunoById(Long idAluno) {
        Aluno aluno = alunoRepository.findById(idAluno)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

        List<Matricula> matriculas = matriculaRepository.findAllByAluno_Id(idAluno);
        List<Long> planoUnidadeIds = matriculas.stream()
                .map(Matricula::getPlanoUnidade)
                .filter(Objects::nonNull)
                .map(PlanoUnidade::getId)
                .distinct()
                .toList();
        List<PlanoUnidadeModalidade> vinculos = planoUnidadeIds.isEmpty()
                ? Collections.emptyList()
                : planoUnidadeModalidadeRepository.findAllByPlanoUnidade_IdIn(planoUnidadeIds);

        return toDetalheDto(aluno, matriculas, vinculos);
    }

    private Pessoa criarPessoa(AlunoForm form) {
        AlunoForm.DadosPessoais dadosPessoais = form.getDadosPessoais();
        AlunoForm.Endereco endereco = form.getEndereco();

        Pessoa pessoa = new Pessoa();
        pessoa.setNome(dadosPessoais.getNome());
        pessoa.setCpf(onlyDigits(dadosPessoais.getCpf()));
        pessoa.setDataNascimento(dadosPessoais.getDataNascimento());
        pessoa.setEmail(dadosPessoais.getEmail());
        pessoa.setTelefone(dadosPessoais.getTelefone());
        pessoa.setSexo(dadosPessoais.getSexo());
        pessoa.setCep(onlyDigits(endereco.getCep()));
        pessoa.setLogradouro(endereco.getLogradouro());
        pessoa.setNumero(endereco.getNumero());
        pessoa.setComplemento(endereco.getComplemento());
        pessoa.setBairro(endereco.getBairro());
        pessoa.setCidade(endereco.getCidade());
        pessoa.setUf(endereco.getUf());
        pessoa.setAtivo(dadosPessoais.getAtivo());
        return pessoa;
    }

    private Aluno criarAluno(AlunoForm form, Pessoa pessoa) {
        Aluno aluno = new Aluno();
        aluno.setPessoa(pessoa);
        aluno.setAtivo(form.getDadosPessoais().getAtivo());
        return aluno;
    }

    private Matricula criarMatricula(AlunoForm form, Aluno aluno, PlanoUnidade planoUnidade) {
        AlunoForm.Matricula matriculaForm = form.getMatricula();

        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setPlanoUnidade(planoUnidade);
        matricula.setDataInicio(matriculaForm.getDataInicio());
        matricula.setDataFim(matriculaForm.getDataFim());
        matricula.setDiaVencimento(matriculaForm.getDiaVencimento());
        matricula.setStatus(matriculaForm.getStatus());
        matricula.setMotivoCancelamento(matriculaForm.getMotivoCancelamento());
        return matricula;
    }

    private String onlyDigits(String value) {
        return value == null ? null : value.replaceAll("\\D", "");
    }

    private String normalizarTexto(String value) {
        return StringUtils.hasText(value) ? value.trim().toLowerCase() : null;
    }

    private Pageable ajustarOrdenacao(Pageable pageable) {
        if (pageable == null || pageable.getSort().isUnsorted()) {
            return pageable;
        }

        Sort sort = Sort.by(pageable.getSort().stream()
                .map(order -> order.withProperty(mapearCampoOrdenacao(order.getProperty())))
                .toList());

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    private String mapearCampoOrdenacao(String property) {
        return switch (property) {
            case "nome" -> "pessoa.nome";
            case "cpf" -> "pessoa.cpf";
            case "email" -> "pessoa.email";
            case "telefone" -> "pessoa.telefone";
            default -> property;
        };
    }

    private Map<Long, List<Matricula>> buscarMatriculasPorAluno(List<Aluno> alunos) {
        if (alunos.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> alunoIds = alunos.stream()
                .map(Aluno::getId)
                .toList();

        return matriculaRepository.findAllByAluno_IdIn(alunoIds)
                .stream()
                .collect(Collectors.groupingBy(matricula -> matricula.getAluno().getId()));
    }

    private Map<Long, List<PlanoUnidadeModalidade>> buscarModalidadesPorPlanoUnidade(
            Collection<List<Matricula>> matriculasPorAluno
    ) {
        List<Long> planoUnidadeIds = matriculasPorAluno.stream()
                .flatMap(Collection::stream)
                .map(Matricula::getPlanoUnidade)
                .filter(Objects::nonNull)
                .map(PlanoUnidade::getId)
                .distinct()
                .toList();

        if (planoUnidadeIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return planoUnidadeModalidadeRepository.findAllByPlanoUnidade_IdIn(planoUnidadeIds)
                .stream()
                .collect(Collectors.groupingBy(vinculo -> vinculo.getPlanoUnidade().getId()));
    }

    private Matricula matriculaPrincipal(List<Matricula> matriculas) {
        return matriculas.stream()
                .max(Comparator.comparing(Matricula::getDataInicio, Comparator.nullsFirst(Comparator.naturalOrder())))
                .orElse(null);
    }

    private AlunoListagemDto toDto(
            Aluno aluno,
            List<Matricula> matriculas,
            Map<Long, List<PlanoUnidadeModalidade>> modalidadesPorPlanoUnidade
    ) {
        Pessoa pessoa = aluno.getPessoa();
        Matricula principal = matriculaPrincipal(matriculas);
        Plano plano = principal != null && principal.getPlanoUnidade() != null
                ? principal.getPlanoUnidade().getPlano()
                : null;

        return new AlunoListagemDto(
                aluno.getId(),
                pessoa != null ? pessoa.getNome() : null,
                pessoa != null ? pessoa.getCpf() : null,
                pessoa != null ? pessoa.getEmail() : null,
                pessoa != null ? pessoa.getTelefone() : null,
                aluno.getAtivo(),
                listarNomesUnidades(matriculas),
                plano != null ? plano.getNome() : null,
                listarNomesModalidades(matriculas, modalidadesPorPlanoUnidade),
                toMatriculaResumoDto(principal)
        );
    }

    private AlunoListagemDto.MatriculaResumoDto toMatriculaResumoDto(Matricula matricula) {
        if (matricula == null) {
            return null;
        }

        return new AlunoListagemDto.MatriculaResumoDto(
                matricula.getId(),
                matricula.getDataInicio(),
                matricula.getDiaVencimento(),
                matricula.getStatus()
        );
    }

    private List<String> listarNomesUnidades(List<Matricula> matriculas) {
        return matriculas.stream()
                .map(Matricula::getPlanoUnidade)
                .filter(Objects::nonNull)
                .map(PlanoUnidade::getUnidade)
                .filter(Objects::nonNull)
                .map(Unidade::getNome)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
    }

    private List<String> listarNomesModalidades(
            List<Matricula> matriculas,
            Map<Long, List<PlanoUnidadeModalidade>> modalidadesPorPlanoUnidade
    ) {
        return matriculas.stream()
                .map(Matricula::getPlanoUnidade)
                .filter(Objects::nonNull)
                .map(PlanoUnidade::getId)
                .distinct()
                .flatMap(id -> modalidadesPorPlanoUnidade.getOrDefault(id, Collections.emptyList()).stream())
                .map(PlanoUnidadeModalidade::getUnidadeModalidade)
                .filter(Objects::nonNull)
                .map(UnidadeModalidade::getModalidade)
                .filter(Objects::nonNull)
                .map(Modalidade::getNome)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
    }

    private AlunoDetalheDto toDetalheDto(
            Aluno aluno,
            List<Matricula> matriculas,
            List<PlanoUnidadeModalidade> vinculos
    ) {
        Pessoa pessoa = aluno.getPessoa();
        Matricula principal = matriculaPrincipal(matriculas);

        return new AlunoDetalheDto(
                aluno.getId(),
                pessoa != null ? pessoa.getNome() : null,
                pessoa != null ? pessoa.getCpf() : null,
                pessoa != null ? pessoa.getEmail() : null,
                pessoa != null ? pessoa.getTelefone() : null,
                pessoa != null ? pessoa.getDataNascimento() : null,
                pessoa != null ? pessoa.getSexo() : null,
                toEnderecoDto(pessoa),
                aluno.getAtivo(),
                listarUnidadesVinculadas(matriculas),
                listarNomesModalidades(vinculos),
                toMatriculaDetalheDto(principal)
        );
    }

    private AlunoDetalheDto.EnderecoDto toEnderecoDto(Pessoa pessoa) {
        if (pessoa == null) {
            return null;
        }

        return new AlunoDetalheDto.EnderecoDto(
                pessoa.getCep(),
                pessoa.getLogradouro(),
                pessoa.getNumero(),
                pessoa.getComplemento(),
                pessoa.getBairro(),
                pessoa.getCidade(),
                pessoa.getUf()
        );
    }

    private List<AlunoDetalheDto.UnidadeVinculadaDto> listarUnidadesVinculadas(List<Matricula> matriculas) {
        return matriculas.stream()
                .map(Matricula::getPlanoUnidade)
                .filter(Objects::nonNull)
                .map(PlanoUnidade::getUnidade)
                .filter(Objects::nonNull)
                .map(unidade -> new AlunoDetalheDto.UnidadeVinculadaDto(
                        unidade.getEstabelecimento() != null ? unidade.getEstabelecimento().getNome() : null,
                        unidade.getNome()
                ))
                .distinct()
                .toList();
    }

    private List<String> listarNomesModalidades(List<PlanoUnidadeModalidade> vinculos) {
        return vinculos.stream()
                .map(PlanoUnidadeModalidade::getUnidadeModalidade)
                .filter(Objects::nonNull)
                .map(UnidadeModalidade::getModalidade)
                .filter(Objects::nonNull)
                .map(Modalidade::getNome)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
    }

    private AlunoDetalheDto.MatriculaDetalheDto toMatriculaDetalheDto(Matricula matricula) {
        if (matricula == null) {
            return null;
        }

        PlanoUnidade planoUnidade = matricula.getPlanoUnidade();
        Unidade unidade = planoUnidade != null ? planoUnidade.getUnidade() : null;
        Plano plano = planoUnidade != null ? planoUnidade.getPlano() : null;

        return new AlunoDetalheDto.MatriculaDetalheDto(
                matricula.getId(),
                unidade != null ? unidade.getNome() : null,
                plano != null ? plano.getNome() : null,
                matricula.getDataInicio(),
                matricula.getDataFim(),
                matricula.getDiaVencimento(),
                matricula.getStatus(),
                matricula.getMotivoCancelamento()
        );
    }
}
