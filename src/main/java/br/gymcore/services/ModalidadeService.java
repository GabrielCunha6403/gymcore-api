package br.gymcore.services;

import br.gymcore.dtos.ModalidadeListagemDto;
import br.gymcore.entities.Estabelecimento;
import br.gymcore.entities.Modalidade;
import br.gymcore.forms.ModalidadeForm;
import br.gymcore.repositories.EstabelecimentoRepository;
import br.gymcore.repositories.ModalidadeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ModalidadeService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final ModalidadeRepository modalidadeRepository;

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

    public List<ModalidadeListagemDto> listar(Long idEstabelecimento, String busca) {
        busca = normalizarBusca(busca);

        return modalidadeRepository.listarComFiltros(idEstabelecimento, busca)
                .stream()
                .map(this::toDto)
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
