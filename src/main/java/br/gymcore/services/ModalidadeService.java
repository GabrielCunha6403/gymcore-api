package br.gymcore.services;

import br.gymcore.dtos.ModalidadeGeralListagemDto;
import br.gymcore.dtos.ModalidadeListagemDto;
import br.gymcore.dtos.PageDto;
import br.gymcore.entities.Estabelecimento;
import br.gymcore.entities.Modalidade;
import br.gymcore.entities.Pessoa;
import br.gymcore.entities.Professor;
import br.gymcore.entities.ProfessorUnidade;
import br.gymcore.entities.ProfessorUnidadeModalidade;
import br.gymcore.entities.Unidade;
import br.gymcore.entities.UnidadeModalidade;
import br.gymcore.forms.ModalidadeForm;
import br.gymcore.repositories.EstabelecimentoRepository;
import br.gymcore.repositories.ModalidadeRepository;
import br.gymcore.repositories.ProfessorUnidadeModalidadeRepository;
import br.gymcore.repositories.UnidadeModalidadeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ModalidadeService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final ModalidadeRepository modalidadeRepository;
    private final UnidadeModalidadeRepository unidadeModalidadeRepository;
    private final ProfessorUnidadeModalidadeRepository professorUnidadeModalidadeRepository;

    @Transactional
    public Long cadastrar(ModalidadeForm form) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(form.getIdEstabelecimento())
                .orElseThrow(() -> new EntityNotFoundException("Estabelecimento não encontrado"));

        Modalidade modalidade = new Modalidade();
        modalidade.setEstabelecimento(estabelecimento);
        modalidade.setNome(form.getNome());
        modalidade.setDescricao(form.getDescricao());
        modalidade.setAtivo(form.getAtivo() != null ? form.getAtivo() : Boolean.TRUE);

        return modalidadeRepository.save(modalidade).getId();
    }

    public ModalidadeListagemDto getModalidadeById(Long idModalidade) {
        Modalidade modalidade = modalidadeRepository.findById(idModalidade).orElseThrow();
        return toDto(modalidade);
    }

    @Transactional
    public void atualizar(Long idModalidade, ModalidadeForm form) {
        Modalidade modalidade = modalidadeRepository.findById(idModalidade)
                .orElseThrow(() -> new EntityNotFoundException("Modalidade não encontrada"));

        modalidade.setNome(form.getNome());
        modalidade.setDescricao(form.getDescricao());
        modalidade.setAtivo(form.getAtivo() != null ? form.getAtivo() : Boolean.TRUE);
    }

    public List<ModalidadeListagemDto> listar(Long idEstabelecimento, String busca) {
        busca = normalizarBusca(busca);

        return modalidadeRepository.listarComFiltros(idEstabelecimento, busca)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageDto<ModalidadeGeralListagemDto> listarGeral(String busca, Pageable pageable) {
        Page<Modalidade> modalidades = modalidadeRepository.listarGeralComFiltros(normalizarBusca(busca), pageable);

        Map<Long, List<UnidadeModalidade>> unidadesPorModalidade = buscarUnidadesPorModalidade(modalidades.getContent());
        Map<Long, List<ProfessorUnidadeModalidade>> professoresPorModalidade = buscarProfessoresPorModalidade(unidadesPorModalidade.values());

        Page<ModalidadeGeralListagemDto> modalidadesDto = modalidades.map(modalidade -> toGeralDto(
                modalidade,
                unidadesPorModalidade.getOrDefault(modalidade.getId(), Collections.emptyList()),
                professoresPorModalidade.getOrDefault(modalidade.getId(), Collections.emptyList())
        ));

        return PageDto.from(modalidadesDto);
    }

    private Map<Long, List<UnidadeModalidade>> buscarUnidadesPorModalidade(List<Modalidade> modalidades) {
        if (modalidades.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> idsModalidade = modalidades.stream().map(Modalidade::getId).toList();

        return unidadeModalidadeRepository.findAllByModalidade_IdIn(idsModalidade)
                .stream()
                .collect(Collectors.groupingBy(vinculo -> vinculo.getModalidade().getId()));
    }

    private Map<Long, List<ProfessorUnidadeModalidade>> buscarProfessoresPorModalidade(
            Collection<List<UnidadeModalidade>> unidadesPorModalidade
    ) {
        List<Long> idsUnidadeModalidade = unidadesPorModalidade.stream()
                .flatMap(Collection::stream)
                .map(UnidadeModalidade::getId)
                .toList();

        if (idsUnidadeModalidade.isEmpty()) {
            return Collections.emptyMap();
        }

        return professorUnidadeModalidadeRepository.findAllByUnidadeModalidade_IdIn(idsUnidadeModalidade)
                .stream()
                .collect(Collectors.groupingBy(vinculo -> vinculo.getUnidadeModalidade().getModalidade().getId()));
    }

    private ModalidadeGeralListagemDto toGeralDto(
            Modalidade modalidade,
            List<UnidadeModalidade> unidades,
            List<ProfessorUnidadeModalidade> professores
    ) {
        Estabelecimento estabelecimento = modalidade.getEstabelecimento();

        return new ModalidadeGeralListagemDto(
                modalidade.getId(),
                modalidade.getNome(),
                modalidade.getDescricao(),
                modalidade.getAtivo(),
                estabelecimento != null ? estabelecimento.getNome() : null,
                listarNomesUnidades(unidades),
                listarNomesProfessores(professores)
        );
    }

    private List<String> listarNomesUnidades(List<UnidadeModalidade> unidades) {
        return unidades.stream()
                .map(UnidadeModalidade::getUnidade)
                .filter(Objects::nonNull)
                .map(Unidade::getNome)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
    }

    private List<String> listarNomesProfessores(List<ProfessorUnidadeModalidade> professores) {
        return professores.stream()
                .map(ProfessorUnidadeModalidade::getProfessorUnidade)
                .filter(Objects::nonNull)
                .map(ProfessorUnidade::getProfessor)
                .filter(Objects::nonNull)
                .map(Professor::getPessoa)
                .filter(Objects::nonNull)
                .map(Pessoa::getNome)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
    }

    private String normalizarBusca(String value) {
        return StringUtils.hasText(value) ? value.trim().toLowerCase() : "";
    }

    private ModalidadeListagemDto toDto(Modalidade modalidade) {
        Estabelecimento estabelecimento = modalidade.getEstabelecimento();

        return new ModalidadeListagemDto(
                String.valueOf(modalidade.getId()),
                estabelecimento != null ? String.valueOf(estabelecimento.getId()) : null,
                modalidade.getNome(),
                modalidade.getDescricao(),
                modalidade.getAtivo()
        );
    }
}
