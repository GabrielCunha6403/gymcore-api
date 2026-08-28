package br.gymcore.services;

import br.gymcore.dtos.PageDto;
import br.gymcore.dtos.ProfessorListagemDto;
import br.gymcore.entities.Modalidade;
import br.gymcore.entities.Pessoa;
import br.gymcore.entities.Professor;
import br.gymcore.entities.ProfessorUnidade;
import br.gymcore.entities.ProfessorUnidadeModalidade;
import br.gymcore.entities.Unidade;
import br.gymcore.entities.UnidadeModalidade;
import br.gymcore.forms.ProfessorForm;
import br.gymcore.repositories.PessoaRepository;
import br.gymcore.repositories.ProfessorRepository;
import br.gymcore.repositories.ProfessorUnidadeModalidadeRepository;
import br.gymcore.repositories.ProfessorUnidadeRepository;
import br.gymcore.repositories.UnidadeModalidadeRepository;
import br.gymcore.repositories.UnidadeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Collections;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final PessoaRepository pessoaRepository;
    private final ProfessorRepository professorRepository;
    private final ProfessorUnidadeRepository professorUnidadeRepository;
    private final ProfessorUnidadeModalidadeRepository professorUnidadeModalidadeRepository;
    private final UnidadeRepository unidadeRepository;
    private final UnidadeModalidadeRepository unidadeModalidadeRepository;

    @Transactional
    public Long cadastrar(ProfessorForm form) {
        Unidade unidade = unidadeRepository.findById(form.getAtuacao().getUnidadeId())
                .orElseThrow(() -> new EntityNotFoundException("Unidade não encontrada"));

        Long estabelecimentoId = unidade.getEstabelecimento() != null
                ? unidade.getEstabelecimento().getId()
                : null;

        if (!Objects.equals(estabelecimentoId, form.getAtuacao().getEstabelecimentoId())) {
            throw new IllegalArgumentException("Unidade não pertence ao estabelecimento informado");
        }

        Pessoa pessoa = pessoaRepository.save(criarPessoa(form));
        Professor professor = professorRepository.save(criarProfessor(form, pessoa));
        ProfessorUnidade professorUnidade = professorUnidadeRepository.save(criarProfessorUnidade(form, professor, unidade));

        vincularModalidades(form, professorUnidade);

        return professor.getId();
    }

    @Transactional(readOnly = true)
    public PageDto<ProfessorListagemDto> listar(String busca, Pageable pageable) {
        String buscaNormalizada = normalizarTexto(busca);
        Pageable pageableOrdenado = ajustarOrdenacao(pageable);

        Page<Professor> professores = buscaNormalizada == null
                ? professorRepository.findAll(pageableOrdenado)
                : professorRepository.listarComFiltros(buscaNormalizada, pageableOrdenado);

        Map<Long, List<ProfessorUnidade>> atuacoesPorProfessor = buscarAtuacoesPorProfessor(professores.getContent());
        Map<Long, List<ProfessorUnidadeModalidade>> modalidadesPorAtuacao = buscarModalidadesPorAtuacao(atuacoesPorProfessor.values());

        Page<ProfessorListagemDto> professoresDto = professores.map(professor -> toDto(
                professor,
                atuacoesPorProfessor.getOrDefault(professor.getId(), Collections.emptyList()),
                modalidadesPorAtuacao
        ));

        return PageDto.from(professoresDto);
    }

    private Pessoa criarPessoa(ProfessorForm form) {
        ProfessorForm.DadosPessoais dadosPessoais = form.getDadosPessoais();
        ProfessorForm.Endereco endereco = form.getEndereco();

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
        pessoa.setAtivo(true);
        return pessoa;
    }

    private Professor criarProfessor(ProfessorForm form, Pessoa pessoa) {
        Professor professor = new Professor();
        professor.setPessoa(pessoa);
        professor.setRegistroProfissional(form.getProfissional().getRegistroProfissional());
        professor.setObservacoes(form.getProfissional().getObservacoes());
        professor.setAtivo(form.getProfissional().getAtivo());
        return professor;
    }

    private ProfessorUnidade criarProfessorUnidade(ProfessorForm form, Professor professor, Unidade unidade) {
        ProfessorUnidade professorUnidade = new ProfessorUnidade();
        professorUnidade.setProfessor(professor);
        professorUnidade.setUnidade(unidade);
        professorUnidade.setCodigo(form.getAtuacao().getCodigoInterno());
        professorUnidade.setAtivo(form.getAtuacao().getAtivo());
        return professorUnidade;
    }

    private void vincularModalidades(ProfessorForm form, ProfessorUnidade professorUnidade) {
        List<Long> modalidades = form.getAtuacao().getModalidades();

        if (CollectionUtils.isEmpty(modalidades)) {
            return;
        }

        List<UnidadeModalidade> unidadeModalidades = unidadeModalidadeRepository
                .findAllByIdInAndUnidade_Id(modalidades, form.getAtuacao().getUnidadeId());

        if (unidadeModalidades.size() != modalidades.size()) {
            throw new IllegalArgumentException("Uma ou mais modalidades nao pertencem a unidade informada");
        }

        List<ProfessorUnidadeModalidade> vinculos = unidadeModalidades.stream()
                .map(unidadeModalidade -> {
                    ProfessorUnidadeModalidade vinculo = new ProfessorUnidadeModalidade();
                    vinculo.setProfessorUnidade(professorUnidade);
                    vinculo.setUnidadeModalidade(unidadeModalidade);
                    vinculo.setAtivo(true);
                    return vinculo;
                })
                .toList();

        professorUnidadeModalidadeRepository.saveAll(vinculos);
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

    private Map<Long, List<ProfessorUnidade>> buscarAtuacoesPorProfessor(List<Professor> professores) {
        if (professores.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> professorIds = professores.stream()
                .map(Professor::getId)
                .toList();

        return professorUnidadeRepository.findAllByProfessor_IdIn(professorIds)
                .stream()
                .collect(Collectors.groupingBy(professorUnidade -> professorUnidade.getProfessor().getId()));
    }

    private Map<Long, List<ProfessorUnidadeModalidade>> buscarModalidadesPorAtuacao(
            Collection<List<ProfessorUnidade>> atuacoesPorProfessor
    ) {
        List<Long> professorUnidadeIds = atuacoesPorProfessor.stream()
                .flatMap(Collection::stream)
                .map(ProfessorUnidade::getId)
                .toList();

        if (professorUnidadeIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return professorUnidadeModalidadeRepository.findAllByProfessorUnidade_IdIn(professorUnidadeIds)
                .stream()
                .collect(Collectors.groupingBy(vinculo -> vinculo.getProfessorUnidade().getId()));
    }

    private ProfessorListagemDto toDto(
            Professor professor,
            List<ProfessorUnidade> atuacoes,
            Map<Long, List<ProfessorUnidadeModalidade>> modalidadesPorAtuacao
    ) {
        Pessoa pessoa = professor.getPessoa();

        return new ProfessorListagemDto(
                professor.getId(),
                pessoa != null ? pessoa.getNome() : null,
                pessoa != null ? pessoa.getCpf() : null,
                pessoa != null ? pessoa.getEmail() : null,
                pessoa != null ? pessoa.getTelefone() : null,
                listarNomesUnidades(atuacoes),
                listarNomesModalidades(atuacoes, modalidadesPorAtuacao),
                professor.getAtivo()
        );
    }

    private List<String> listarNomesUnidades(List<ProfessorUnidade> atuacoes) {
        return atuacoes.stream()
                .map(ProfessorUnidade::getUnidade)
                .filter(Objects::nonNull)
                .map(Unidade::getNome)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
    }

    private List<String> listarNomesModalidades(
            List<ProfessorUnidade> atuacoes,
            Map<Long, List<ProfessorUnidadeModalidade>> modalidadesPorAtuacao
    ) {
        return atuacoes.stream()
                .flatMap(atuacao -> modalidadesPorAtuacao.getOrDefault(atuacao.getId(), Collections.emptyList()).stream())
                .map(ProfessorUnidadeModalidade::getUnidadeModalidade)
                .filter(Objects::nonNull)
                .map(UnidadeModalidade::getModalidade)
                .filter(Objects::nonNull)
                .map(Modalidade::getNome)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
    }
}
