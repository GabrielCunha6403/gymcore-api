package br.gymcore.services;

import br.gymcore.dtos.UnidadeListagemDto;
import br.gymcore.entities.Estabelecimento;
import br.gymcore.entities.TipoEstabelecimento;
import br.gymcore.entities.Unidade;
import br.gymcore.enums.EstabelecimentoStatus;
import br.gymcore.forms.UnidadeForm;
import br.gymcore.repositories.EstabelecimentoRepository;
import br.gymcore.repositories.UnidadeRepository;
import jakarta.persistence.EntityNotFoundException;
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
public class UnidadeService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final UnidadeRepository unidadeRepository;

    @Transactional
    public Long cadastrar(UnidadeForm form) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(form.getIdEstabelecimento())
                .orElseThrow(() -> new EntityNotFoundException("Estabelecimento não encontrado"));

        Unidade unidade = new Unidade();
        unidade.setEstabelecimento(estabelecimento);
        unidade.setNome(form.getNome());
        unidade.setCnpj(onlyDigits(form.getCnpj()));
        unidade.setEmail(form.getEmail());
        unidade.setTelefone(form.getTelefone());
        unidade.setCep(onlyDigits(form.getEndereco().getCep()));
        unidade.setLogradouro(form.getEndereco().getLogradouro());
        unidade.setNumero(form.getEndereco().getNumero());
        unidade.setComplemento(form.getEndereco().getComplemento());
        unidade.setBairro(form.getEndereco().getBairro());
        unidade.setCidade(form.getEndereco().getCidade());
        unidade.setUf(form.getEndereco().getUf());
        unidade.setAtivo(form.getAtivo() != null ? form.getAtivo() : Boolean.TRUE);

        return unidadeRepository.save(unidade).getId();
    }

    public UnidadeListagemDto getUnidadeById(Long idUnidade) {
        Unidade unidade = unidadeRepository.findById(idUnidade).orElseThrow();
        unidade.setEstabelecimento(estabelecimentoRepository.findById(unidade.getEstabelecimento().getId()).orElse(null));
        return toDto(unidade, false);
    }

    public List<UnidadeListagemDto> listar(Long idEstabelecimento, String busca) {
        busca = normalizarBusca(busca);

        List<Unidade> unidades = unidadeRepository.listarComFiltros(idEstabelecimento, busca);
        Map<Long, Long> matrizIdPorEstabelecimento = buscarMatrizIdPorEstabelecimento(unidades);

        return unidades.stream()
                .map(unidade -> toDto(unidade, isMatriz(unidade, matrizIdPorEstabelecimento)))
                .toList();
    }

    private String onlyDigits(String value) {
        return value == null ? null : value.replaceAll("\\D", "");
    }

    private String normalizarBusca(String value) {
        return StringUtils.hasText(value) ? value.trim().toLowerCase() : "";
    }

    private Map<Long, Long> buscarMatrizIdPorEstabelecimento(List<Unidade> unidades) {
        List<Long> estabelecimentoIds = unidades.stream()
                .map(Unidade::getEstabelecimento)
                .filter(Objects::nonNull)
                .map(Estabelecimento::getId)
                .distinct()
                .toList();

        if (estabelecimentoIds.isEmpty()) {
            return Map.of();
        }

        return unidadeRepository.findAllByEstabelecimento_IdIn(estabelecimentoIds)
                .stream()
                .filter(unidade -> unidade.getEstabelecimento() != null)
                .collect(Collectors.toMap(
                        unidade -> unidade.getEstabelecimento().getId(),
                        Unidade::getId,
                        (idExistente, idNovo) -> idExistente
                ));
    }

    private boolean isMatriz(Unidade unidade, Map<Long, Long> matrizIdPorEstabelecimento) {
        Estabelecimento estabelecimento = unidade.getEstabelecimento();

        if (estabelecimento == null) {
            return false;
        }

        return Objects.equals(unidade.getId(), matrizIdPorEstabelecimento.get(estabelecimento.getId()));
    }

    private UnidadeListagemDto toDto(Unidade unidade, boolean matriz) {
        Estabelecimento estabelecimento = unidade.getEstabelecimento();
        TipoEstabelecimento tipoEstabelecimento = estabelecimento != null
                ? estabelecimento.getTipoEstabelecimento()
                : null;

        return new UnidadeListagemDto(
                String.valueOf(unidade.getId()),
                estabelecimento != null ? String.valueOf(estabelecimento.getId()) : null,
                unidade.getNome(),
                null,
                unidade.getCnpj(),
                tipoEstabelecimento != null ? tipoEstabelecimento.getCodigo() : null,
                unidade.getEmail(),
                unidade.getTelefone(),
                toEnderecoDto(unidade),
                resolverStatus(unidade, estabelecimento),
                matriz
        );
    }

    private UnidadeListagemDto.EnderecoDto toEnderecoDto(Unidade unidade) {
        return new UnidadeListagemDto.EnderecoDto(
                unidade.getCep(),
                unidade.getLogradouro(),
                unidade.getNumero(),
                unidade.getComplemento(),
                unidade.getBairro(),
                unidade.getCidade(),
                unidade.getUf()
        );
    }

    private EstabelecimentoStatus resolverStatus(Unidade unidade, Estabelecimento estabelecimento) {
        if (Boolean.FALSE.equals(unidade.getAtivo())) {
            return EstabelecimentoStatus.INATIVO;
        }

        return estabelecimento != null && estabelecimento.getStatus() != null
                ? estabelecimento.getStatus()
                : EstabelecimentoStatus.ATIVO;
    }
}
