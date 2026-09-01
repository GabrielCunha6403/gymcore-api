package br.gymcore.services;

import br.gymcore.dtos.PlanoListagemDto;
import br.gymcore.entities.Estabelecimento;
import br.gymcore.entities.Plano;
import br.gymcore.forms.PlanoForm;
import br.gymcore.repositories.EstabelecimentoRepository;
import br.gymcore.repositories.PlanoRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PlanoService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final PlanoRepository planoRepository;

    @Transactional
    public Long cadastrar(PlanoForm form) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(form.getIdEstabelecimento())
                .orElseThrow(() -> new EntityNotFoundException("Estabelecimento não encontrado"));

        Plano plano = new Plano();
        plano.setEstabelecimento(estabelecimento);
        plano.setNome(form.getNome());
        plano.setDescricao(form.getDescricao());
        plano.setAtivo(form.getAtivo() != null ? form.getAtivo() : Boolean.TRUE);

        return planoRepository.save(plano).getId();
    }

    public PlanoListagemDto getPlanoById(Long idPlano) {
        Plano plano = planoRepository.findById(idPlano).orElseThrow();
        return toDto(plano);
    }

    public List<PlanoListagemDto> listar(Long idEstabelecimento, String busca) {
        busca = normalizarBusca(busca);

        return planoRepository.listarComFiltros(idEstabelecimento, busca)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private String normalizarBusca(String value) {
        return StringUtils.hasText(value) ? value.trim().toLowerCase() : "";
    }

    private PlanoListagemDto toDto(Plano plano) {
        Estabelecimento estabelecimento = plano.getEstabelecimento();

        return new PlanoListagemDto(
                String.valueOf(plano.getId()),
                estabelecimento != null ? String.valueOf(estabelecimento.getId()) : null,
                plano.getNome(),
                plano.getDescricao(),
                plano.getAtivo()
        );
    }
}
