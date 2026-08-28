package br.gymcore.services;

import br.gymcore.dtos.EstabelecimentoListagemDto;
import br.gymcore.entities.Estabelecimento;
import br.gymcore.entities.TipoEstabelecimento;
import br.gymcore.entities.Unidade;
import br.gymcore.enums.EstabelecimentoStatus;
import br.gymcore.forms.EstabelecimentoForm;
import br.gymcore.repositories.EstabelecimentoRepository;
import br.gymcore.repositories.TipoEstabelecimentoRepository;
import br.gymcore.repositories.UnidadeRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final TipoEstabelecimentoRepository tipoEstabelecimentoRepository;
    private final UnidadeRepository unidadeRepository;

    @Transactional
    public Long cadastrar(EstabelecimentoForm form) {
        TipoEstabelecimento tipoEstabelecimento = tipoEstabelecimentoRepository
                .findByCodigoAndAtivoTrue(form.getTipo())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de estabelecimento nao encontrado"));

        Estabelecimento estabelecimento = new Estabelecimento();
        estabelecimento.setNome(form.getNome());
        estabelecimento.setRazaoSocial(form.getRazaoSocial());
        estabelecimento.setEmail(form.getEmail());
        estabelecimento.setTelefone(form.getTelefone());
        estabelecimento.setSite(form.getSite());
        estabelecimento.setLogoUrl(form.getLogoUrl());
        estabelecimento.setTipoEstabelecimento(tipoEstabelecimento);
        estabelecimento.setStatus(form.getStatus() != null ? form.getStatus() : EstabelecimentoStatus.ATIVO);
        estabelecimento.setAtivo(form.getAtivo() != null ? form.getAtivo() : Boolean.TRUE);

        return estabelecimentoRepository.save(estabelecimento).getId();
    }

    public EstabelecimentoListagemDto getEstabelecimentoById(Long idEstabelecimento) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(idEstabelecimento).orElseThrow();
        return this.toDto(
                estabelecimento,
                buscarUnidadesPorEstabelecimento(List.of(estabelecimento)).getOrDefault(idEstabelecimento, Collections.emptyList())
        );
    }

    public List<EstabelecimentoListagemDto> listar(String busca) {
        busca = normalizarBusca(busca);

        List<Estabelecimento> estabelecimentos = estabelecimentoRepository.listarComFiltros(busca);
        Map<Long, List<Unidade>> unidadesPorEstabelecimento = buscarUnidadesPorEstabelecimento(estabelecimentos);

        return estabelecimentos.stream()
                .map(estabelecimento -> toDto(
                        estabelecimento,
                        unidadesPorEstabelecimento.getOrDefault(estabelecimento.getId(), Collections.emptyList())
                ))
                .toList();
    }

    private String normalizarBusca(String value) {
        return StringUtils.hasText(value) ? value.trim().toLowerCase() : "";
    }

    private Map<Long, List<Unidade>> buscarUnidadesPorEstabelecimento(List<Estabelecimento> estabelecimentos) {
        if (estabelecimentos.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> estabelecimentoIds = estabelecimentos.stream()
                .map(Estabelecimento::getId)
                .toList();

        return unidadeRepository.findAllByEstabelecimento_IdIn(estabelecimentoIds)
                .stream()
                .filter(unidade -> unidade.getEstabelecimento() != null)
                .collect(Collectors.groupingBy(unidade -> unidade.getEstabelecimento().getId()));
    }

    private EstabelecimentoListagemDto toDto(Estabelecimento estabelecimento, List<Unidade> unidades) {
        return new EstabelecimentoListagemDto(
                String.valueOf(estabelecimento.getId()),
                estabelecimento.getNome(),
                estabelecimento.getRazaoSocial(),
                estabelecimento.getLogoUrl(),
                buscarCnpjPrincipal(unidades),
                estabelecimento.getEmail(),
                estabelecimento.getTelefone(),
                estabelecimento.getSite(),
                estabelecimento.getStatus(),
                unidades.size()
        );
    }

    private String buscarCnpjPrincipal(List<Unidade> unidades) {
        return unidades.stream()
                .map(Unidade::getCnpj)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
    }
}
