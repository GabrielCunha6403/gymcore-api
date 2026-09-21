package br.gymcore.services;

import br.gymcore.dtos.PageDto;
import br.gymcore.dtos.ProfessorDetalheDto;
import br.gymcore.dtos.ProfessorListagemDto;
import br.gymcore.entities.Modalidade;
import br.gymcore.entities.Pessoa;
import br.gymcore.entities.Professor;
import br.gymcore.entities.ProfessorUnidade;
import br.gymcore.entities.ProfessorUnidadeModalidade;
import br.gymcore.entities.Unidade;
import br.gymcore.entities.UnidadeModalidade;
import br.gymcore.enums.ProfessorStatus;
import br.gymcore.forms.ProfessorForm;
import br.gymcore.repositories.PessoaRepository;
import br.gymcore.repositories.ProfessorRepository;
import br.gymcore.repositories.ProfessorUnidadeModalidadeRepository;
import br.gymcore.repositories.ProfessorUnidadeRepository;
import br.gymcore.repositories.UnidadeModalidadeRepository;
import br.gymcore.repositories.UnidadeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
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

        List<UnidadeModalidade> modalidades = buscarModalidadesDaUnidade(
                form.getAtuacao().getUnidadeId(), form.getAtuacao().getModalidades());

        Pessoa pessoa = pessoaRepository.save(criarPessoa(form));
        Professor professor = professorRepository.save(criarProfessor(form, pessoa));
        ProfessorUnidade professorUnidade = professorUnidadeRepository.save(criarProfessorUnidade(form, professor, unidade));

        vincularModalidades(professorUnidade, modalidades);

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

    @Transactional(readOnly = true)
    public List<ProfessorListagemDto> listarPorUnidade(Long idUnidade) {
        List<ProfessorUnidade> atuacoes = professorUnidadeRepository.findAllByUnidade_Id(idUnidade);

        if (atuacoes.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> professorUnidadeIds = atuacoes.stream().map(ProfessorUnidade::getId).toList();
        Map<Long, List<ProfessorUnidadeModalidade>> modalidadesPorAtuacao = professorUnidadeModalidadeRepository
                .findAllByProfessorUnidade_IdIn(professorUnidadeIds)
                .stream()
                .collect(Collectors.groupingBy(vinculo -> vinculo.getProfessorUnidade().getId()));

        return atuacoes.stream()
                .map(atuacao -> toDtoPorUnidade(atuacao, modalidadesPorAtuacao.getOrDefault(atuacao.getId(), Collections.emptyList())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProfessorListagemDto> listarPorUnidadeModalidade(Long idUnidadeModalidade) {
        List<ProfessorUnidadeModalidade> vinculosModalidade = professorUnidadeModalidadeRepository
                .findAllByUnidadeModalidade_IdIn(List.of(idUnidadeModalidade));

        if (vinculosModalidade.isEmpty()) {
            return Collections.emptyList();
        }

        List<ProfessorUnidade> atuacoes = vinculosModalidade.stream()
                .map(ProfessorUnidadeModalidade::getProfessorUnidade)
                .distinct()
                .toList();

        List<Long> professorUnidadeIds = atuacoes.stream().map(ProfessorUnidade::getId).toList();
        Map<Long, List<ProfessorUnidadeModalidade>> modalidadesPorAtuacao = professorUnidadeModalidadeRepository
                .findAllByProfessorUnidade_IdIn(professorUnidadeIds)
                .stream()
                .collect(Collectors.groupingBy(vinculo -> vinculo.getProfessorUnidade().getId()));

        return atuacoes.stream()
                .map(atuacao -> toDtoPorUnidade(atuacao, modalidadesPorAtuacao.getOrDefault(atuacao.getId(), Collections.emptyList())))
                .toList();
    }

    @Transactional
    public void atualizar(Long idProfessor, ProfessorForm form) {
        Professor professor = professorRepository.findById(idProfessor)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado"));

        Pessoa pessoa = professor.getPessoa();
        ProfessorForm.DadosPessoais dadosPessoais = form.getDadosPessoais();
        ProfessorForm.Endereco endereco = form.getEndereco();

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

        professor.setRegistroProfissional(form.getProfissional().getRegistroProfissional());
        professor.setObservacoes(form.getProfissional().getObservacoes());
        professor.setStatus(form.getProfissional().getStatus());

        List<ProfessorUnidade> atuacoes = professorUnidadeRepository.findAllByProfessor_IdIn(List.of(idProfessor));

        if (!atuacoes.isEmpty()) {
            ProfessorUnidade atuacaoPrincipal = atuacoes.get(0);
            atuacaoPrincipal.setCodigo(form.getAtuacao().getCodigoInterno());
            atuacaoPrincipal.setAtivo(form.getAtuacao().getAtivo());

            atualizarModalidades(atuacaoPrincipal, form.getAtuacao().getModalidades());
        }
    }

    private void atualizarModalidades(ProfessorUnidade professorUnidade, List<Long> modalidades) {
        List<UnidadeModalidade> unidadeModalidades = buscarModalidadesDaUnidade(
                professorUnidade.getUnidade().getId(), modalidades);

        professorUnidadeModalidadeRepository.deleteAllByProfessorUnidade_Id(professorUnidade.getId());
        professorUnidadeModalidadeRepository.flush();
        vincularModalidades(professorUnidade, unidadeModalidades);
    }

    @Transactional
    public void inativar(Long idProfessor) {
        Professor professor = professorRepository.findById(idProfessor)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado"));

        professor.setStatus(ProfessorStatus.INATIVO);
    }

    @Transactional
    public void desligarDaUnidade(Long idProfessor, Long idUnidade) {
        ProfessorUnidade professorUnidade = professorUnidadeRepository
                .findByProfessor_IdAndUnidade_Id(idProfessor, idUnidade)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo do professor com a unidade não encontrado"));

        professorUnidade.setAtivo(Boolean.FALSE);
        professorUnidade.setDataDesligamento(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public ProfessorDetalheDto getProfessorById(Long idProfessor) {
        Professor professor = professorRepository.findById(idProfessor)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado"));

        List<ProfessorUnidade> atuacoes = professorUnidadeRepository.findAllByProfessor_IdIn(List.of(idProfessor));
        List<Long> professorUnidadeIds = atuacoes.stream().map(ProfessorUnidade::getId).toList();
        List<ProfessorUnidadeModalidade> vinculos = professorUnidadeIds.isEmpty()
                ? Collections.emptyList()
                : professorUnidadeModalidadeRepository.findAllByProfessorUnidade_IdIn(professorUnidadeIds);

        return toDetalheDto(professor, atuacoes, vinculos);
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
        professor.setStatus(form.getProfissional().getStatus());
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

    private List<UnidadeModalidade> buscarModalidadesDaUnidade(Long idUnidade, List<Long> idsUnidadeModalidade) {
        if (CollectionUtils.isEmpty(idsUnidadeModalidade)) {
            return Collections.emptyList();
        }

        List<Long> idsUnicos = idsUnidadeModalidade.stream().distinct().toList();
        List<UnidadeModalidade> unidadeModalidades = unidadeModalidadeRepository
                .findAllByIdInAndUnidade_Id(idsUnicos, idUnidade);

        if (unidadeModalidades.size() != idsUnicos.size()) {
            throw new IllegalArgumentException("Uma ou mais modalidades nao pertencem a unidade informada");
        }

        return unidadeModalidades;
    }

    private void vincularModalidades(ProfessorUnidade professorUnidade, List<UnidadeModalidade> unidadeModalidades) {
        if (unidadeModalidades.isEmpty()) {
            return;
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
                professor.getStatus()
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

    private ProfessorListagemDto toDtoPorUnidade(ProfessorUnidade atuacao, List<ProfessorUnidadeModalidade> modalidades) {
        Professor professor = atuacao.getProfessor();
        Pessoa pessoa = professor.getPessoa();
        Unidade unidade = atuacao.getUnidade();

        return new ProfessorListagemDto(
                professor.getId(),
                pessoa != null ? pessoa.getNome() : null,
                pessoa != null ? pessoa.getCpf() : null,
                pessoa != null ? pessoa.getEmail() : null,
                pessoa != null ? pessoa.getTelefone() : null,
                unidade != null && StringUtils.hasText(unidade.getNome()) ? List.of(unidade.getNome()) : Collections.emptyList(),
                listarNomesModalidades(modalidades),
                professor.getStatus()
        );
    }

    private ProfessorDetalheDto toDetalheDto(
            Professor professor,
            List<ProfessorUnidade> atuacoes,
            List<ProfessorUnidadeModalidade> vinculos
    ) {
        Pessoa pessoa = professor.getPessoa();
        ProfessorUnidade atuacaoPrincipal = atuacoes.isEmpty() ? null : atuacoes.get(0);

        return new ProfessorDetalheDto(
                professor.getId(),
                pessoa != null ? pessoa.getNome() : null,
                pessoa != null ? pessoa.getCpf() : null,
                pessoa != null ? pessoa.getEmail() : null,
                pessoa != null ? pessoa.getTelefone() : null,
                pessoa != null ? pessoa.getDataNascimento() : null,
                pessoa != null ? pessoa.getSexo() : null,
                toEnderecoDto(pessoa),
                professor.getRegistroProfissional(),
                professor.getObservacoes(),
                atuacaoPrincipal != null ? atuacaoPrincipal.getCodigo() : null,
                atuacaoPrincipal != null ? atuacaoPrincipal.getAtivo() : null,
                listarUnidadesVinculadas(atuacoes),
                listarNomesModalidades(vinculos),
                professor.getStatus()
        );
    }

    private ProfessorDetalheDto.EnderecoDto toEnderecoDto(Pessoa pessoa) {
        if (pessoa == null) {
            return null;
        }

        return new ProfessorDetalheDto.EnderecoDto(
                pessoa.getCep(),
                pessoa.getLogradouro(),
                pessoa.getNumero(),
                pessoa.getComplemento(),
                pessoa.getBairro(),
                pessoa.getCidade(),
                pessoa.getUf()
        );
    }

    private List<ProfessorDetalheDto.UnidadeVinculadaDto> listarUnidadesVinculadas(List<ProfessorUnidade> atuacoes) {
        return atuacoes.stream()
                .map(ProfessorUnidade::getUnidade)
                .filter(Objects::nonNull)
                .map(unidade -> new ProfessorDetalheDto.UnidadeVinculadaDto(
                        unidade.getEstabelecimento() != null ? unidade.getEstabelecimento().getNome() : null,
                        unidade.getNome()
                ))
                .toList();
    }

    private List<String> listarNomesModalidades(List<ProfessorUnidadeModalidade> vinculos) {
        return vinculos.stream()
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
