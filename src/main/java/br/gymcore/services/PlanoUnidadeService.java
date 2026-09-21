package br.gymcore.services;

import br.gymcore.dtos.PlanoUnidadeDetalheDto;
import br.gymcore.dtos.PlanoUnidadeListagemDto;
import br.gymcore.entities.Estabelecimento;
import br.gymcore.entities.Modalidade;
import br.gymcore.entities.Plano;
import br.gymcore.entities.PlanoUnidade;
import br.gymcore.entities.PlanoUnidadeModalidade;
import br.gymcore.entities.PlanoUnidadeModalidadeId;
import br.gymcore.entities.TipoCobranca;
import br.gymcore.entities.Unidade;
import br.gymcore.entities.UnidadeModalidade;
import br.gymcore.forms.PlanoUnidadeForm;
import br.gymcore.repositories.PlanoRepository;
import br.gymcore.repositories.PlanoUnidadeModalidadeRepository;
import br.gymcore.repositories.PlanoUnidadeRepository;
import br.gymcore.repositories.TipoCobrancaRepository;
import br.gymcore.repositories.UnidadeModalidadeRepository;
import br.gymcore.repositories.UnidadeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PlanoUnidadeService {

    private final UnidadeRepository unidadeRepository;
    private final PlanoRepository planoRepository;
    private final TipoCobrancaRepository tipoCobrancaRepository;
    private final PlanoUnidadeRepository planoUnidadeRepository;
    private final UnidadeModalidadeRepository unidadeModalidadeRepository;
    private final PlanoUnidadeModalidadeRepository planoUnidadeModalidadeRepository;

    @Transactional
    public Long vincular(PlanoUnidadeForm form) {
        Unidade unidade = unidadeRepository.findById(form.getIdUnidade())
                .orElseThrow(() -> new EntityNotFoundException("Unidade não encontrada"));

        Plano plano = planoRepository.findById(form.getIdPlano())
                .orElseThrow(() -> new EntityNotFoundException("Plano não encontrado"));

        if (!Objects.equals(unidade.getEstabelecimento().getId(), plano.getEstabelecimento().getId())) {
            throw new IllegalArgumentException("Plano não pertence ao estabelecimento da unidade");
        }

        if (planoUnidadeRepository.existsByUnidade_IdAndPlano_Id(unidade.getId(), plano.getId())) {
            throw new IllegalStateException("Plano já vinculado a esta unidade");
        }

        TipoCobranca tipoCobranca = tipoCobrancaRepository.findByCodigoAndAtivoTrue(form.getTipoCobranca())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de cobrança não encontrado"));

        List<UnidadeModalidade> modalidades = buscarModalidadesDaUnidade(unidade.getId(), form.getModalidades());

        PlanoUnidade planoUnidade = new PlanoUnidade();
        planoUnidade.setPlano(plano);
        planoUnidade.setUnidade(unidade);
        planoUnidade.setNomeExibicao(form.getNomeExibicao());
        planoUnidade.setDescricao(form.getDescricao());
        planoUnidade.setValor(form.getValor());
        planoUnidade.setDuracaoMeses(form.getDuracaoMeses());
        planoUnidade.setTipoCobranca(tipoCobranca);
        planoUnidade.setTaxaAdesao(form.getTaxaAdesao());
        planoUnidade.setDiaVencimentoPadrao(form.getDiaVencimentoPadrao());
        planoUnidade.setAtivo(form.getAtivo() != null ? form.getAtivo() : Boolean.TRUE);

        planoUnidade = planoUnidadeRepository.save(planoUnidade);

        vincularModalidades(planoUnidade, modalidades);

        return planoUnidade.getId();
    }

    @Transactional(readOnly = true)
    public PlanoUnidadeDetalheDto getPlanoUnidadeById(Long idPlanoUnidade) {
        PlanoUnidade planoUnidade = planoUnidadeRepository.findById(idPlanoUnidade)
                .orElseThrow(() -> new EntityNotFoundException("Oferta de plano não encontrada"));

        List<PlanoUnidadeModalidade> vinculos = planoUnidadeModalidadeRepository
                .findAllByPlanoUnidade_IdIn(List.of(idPlanoUnidade));

        Plano plano = planoUnidade.getPlano();
        TipoCobranca tipoCobranca = planoUnidade.getTipoCobranca();

        return new PlanoUnidadeDetalheDto(
                String.valueOf(planoUnidade.getId()),
                String.valueOf(planoUnidade.getUnidade().getId()),
                String.valueOf(plano.getId()),
                plano.getNome(),
                planoUnidade.getNomeExibicao(),
                planoUnidade.getDescricao(),
                planoUnidade.getValor(),
                planoUnidade.getDuracaoMeses(),
                tipoCobranca != null ? tipoCobranca.getCodigo() : null,
                planoUnidade.getTaxaAdesao(),
                planoUnidade.getDiaVencimentoPadrao(),
                planoUnidade.getAtivo(),
                vinculos.stream().map(vinculo -> String.valueOf(vinculo.getUnidadeModalidade().getId())).toList()
        );
    }

    @Transactional
    public void atualizar(Long idPlanoUnidade, PlanoUnidadeForm form) {
        PlanoUnidade planoUnidade = planoUnidadeRepository.findById(idPlanoUnidade)
                .orElseThrow(() -> new EntityNotFoundException("Oferta de plano não encontrada"));

        TipoCobranca tipoCobranca = tipoCobrancaRepository.findByCodigoAndAtivoTrue(form.getTipoCobranca())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de cobrança não encontrado"));

        List<UnidadeModalidade> modalidades = buscarModalidadesDaUnidade(planoUnidade.getUnidade().getId(), form.getModalidades());

        planoUnidade.setNomeExibicao(form.getNomeExibicao());
        planoUnidade.setDescricao(form.getDescricao());
        planoUnidade.setValor(form.getValor());
        planoUnidade.setDuracaoMeses(form.getDuracaoMeses());
        planoUnidade.setTipoCobranca(tipoCobranca);
        planoUnidade.setTaxaAdesao(form.getTaxaAdesao());
        planoUnidade.setDiaVencimentoPadrao(form.getDiaVencimentoPadrao());
        planoUnidade.setAtivo(form.getAtivo() != null ? form.getAtivo() : Boolean.TRUE);

        planoUnidadeModalidadeRepository.deleteAllByPlanoUnidade_Id(planoUnidade.getId());
        planoUnidadeModalidadeRepository.flush();
        vincularModalidades(planoUnidade, modalidades);
    }

    @Transactional
    public void inativar(Long idPlanoUnidade) {
        PlanoUnidade planoUnidade = planoUnidadeRepository.findById(idPlanoUnidade)
                .orElseThrow(() -> new EntityNotFoundException("Oferta de plano não encontrada"));

        planoUnidade.setAtivo(Boolean.FALSE);
    }

    @Transactional(readOnly = true)
    public List<PlanoUnidadeListagemDto> listar(Long idUnidade, String busca) {
        List<PlanoUnidade> planosUnidade = idUnidade != null
                ? planoUnidadeRepository.listarPorUnidade(idUnidade)
                : planoUnidadeRepository.listarGeral(normalizarBusca(busca));

        Map<Long, List<PlanoUnidadeModalidade>> modalidadesPorPlanoUnidade = buscarModalidadesPorPlanoUnidade(planosUnidade);

        return planosUnidade.stream()
                .map(planoUnidade -> toDto(
                        planoUnidade,
                        modalidadesPorPlanoUnidade.getOrDefault(planoUnidade.getId(), Collections.emptyList())
                ))
                .toList();
    }

    private String normalizarBusca(String value) {
        return StringUtils.hasText(value) ? value.trim().toLowerCase() : "";
    }

    private List<UnidadeModalidade> buscarModalidadesDaUnidade(Long idUnidade, List<Long> idsUnidadeModalidade) {
        if (idsUnidadeModalidade == null || idsUnidadeModalidade.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> idsUnicos = idsUnidadeModalidade.stream().distinct().toList();
        List<UnidadeModalidade> modalidades = unidadeModalidadeRepository.findAllByIdInAndUnidade_Id(idsUnicos, idUnidade);

        if (modalidades.size() != idsUnicos.size()) {
            throw new EntityNotFoundException("Uma ou mais modalidades informadas não pertencem a esta unidade");
        }

        return modalidades;
    }

    private void vincularModalidades(PlanoUnidade planoUnidade, List<UnidadeModalidade> modalidades) {
        if (modalidades.isEmpty()) {
            return;
        }

        List<PlanoUnidadeModalidade> vinculos = modalidades.stream()
                .map(unidadeModalidade -> {
                    PlanoUnidadeModalidade vinculo = new PlanoUnidadeModalidade();
                    vinculo.setId(new PlanoUnidadeModalidadeId(planoUnidade.getId(), unidadeModalidade.getId()));
                    vinculo.setPlanoUnidade(planoUnidade);
                    vinculo.setUnidadeModalidade(unidadeModalidade);
                    return vinculo;
                })
                .toList();

        planoUnidadeModalidadeRepository.saveAll(vinculos);
    }

    private Map<Long, List<PlanoUnidadeModalidade>> buscarModalidadesPorPlanoUnidade(List<PlanoUnidade> planosUnidade) {
        List<Long> ids = planosUnidade.stream().map(PlanoUnidade::getId).toList();

        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        return planoUnidadeModalidadeRepository.findAllByPlanoUnidade_IdIn(ids)
                .stream()
                .collect(Collectors.groupingBy(vinculo -> vinculo.getPlanoUnidade().getId()));
    }

    private PlanoUnidadeListagemDto toDto(PlanoUnidade planoUnidade, List<PlanoUnidadeModalidade> vinculos) {
        Plano plano = planoUnidade.getPlano();
        Unidade unidade = planoUnidade.getUnidade();
        Estabelecimento estabelecimento = unidade != null ? unidade.getEstabelecimento() : null;
        TipoCobranca tipoCobranca = planoUnidade.getTipoCobranca();

        return new PlanoUnidadeListagemDto(
                String.valueOf(planoUnidade.getId()),
                unidade != null ? String.valueOf(unidade.getId()) : null,
                unidade != null ? unidade.getNome() : null,
                estabelecimento != null ? String.valueOf(estabelecimento.getId()) : null,
                estabelecimento != null ? estabelecimento.getNome() : null,
                String.valueOf(plano.getId()),
                plano.getNome(),
                plano.getDescricao(),
                plano.getAtivo(),
                planoUnidade.getNomeExibicao(),
                planoUnidade.getDescricao(),
                planoUnidade.getValor(),
                planoUnidade.getDuracaoMeses(),
                tipoCobranca != null ? tipoCobranca.getCodigo() : null,
                planoUnidade.getTaxaAdesao(),
                planoUnidade.getDiaVencimentoPadrao(),
                planoUnidade.getAtivo(),
                listarNomesModalidades(vinculos)
        );
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
}
