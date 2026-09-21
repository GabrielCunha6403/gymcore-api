package br.gymcore.services;

import br.gymcore.dtos.UnidadeModalidadeListagemDto;
import br.gymcore.entities.Modalidade;
import br.gymcore.entities.Unidade;
import br.gymcore.entities.UnidadeModalidade;
import br.gymcore.forms.UnidadeModalidadeForm;
import br.gymcore.repositories.ModalidadeRepository;
import br.gymcore.repositories.UnidadeModalidadeRepository;
import br.gymcore.repositories.UnidadeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnidadeModalidadeService {

    private final UnidadeRepository unidadeRepository;
    private final ModalidadeRepository modalidadeRepository;
    private final UnidadeModalidadeRepository unidadeModalidadeRepository;

    @Transactional
    public Long vincular(UnidadeModalidadeForm form) {
        Unidade unidade = unidadeRepository.findById(form.getIdUnidade())
                .orElseThrow(() -> new EntityNotFoundException("Unidade não encontrada"));

        Modalidade modalidade = modalidadeRepository.findById(form.getIdModalidade())
                .orElseThrow(() -> new EntityNotFoundException("Modalidade não encontrada"));

        if (!Objects.equals(unidade.getEstabelecimento().getId(), modalidade.getEstabelecimento().getId())) {
            throw new IllegalArgumentException("Modalidade não pertence ao estabelecimento da unidade");
        }

        if (unidadeModalidadeRepository.existsByUnidade_IdAndModalidade_Id(unidade.getId(), modalidade.getId())) {
            throw new IllegalStateException("Modalidade já vinculada a esta unidade");
        }

        UnidadeModalidade unidadeModalidade = new UnidadeModalidade();
        unidadeModalidade.setUnidade(unidade);
        unidadeModalidade.setModalidade(modalidade);
        unidadeModalidade.setDescricao(form.getDescricao());
        unidadeModalidade.setCapacidadePadrao(form.getCapacidadePadrao());
        unidadeModalidade.setAtivo(form.getAtivo() != null ? form.getAtivo() : Boolean.TRUE);

        return unidadeModalidadeRepository.save(unidadeModalidade).getId();
    }

    @Transactional
    public void atualizar(Long idUnidadeModalidade, UnidadeModalidadeForm form) {
        UnidadeModalidade unidadeModalidade = unidadeModalidadeRepository.findById(idUnidadeModalidade)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo de modalidade não encontrado"));

        unidadeModalidade.setDescricao(form.getDescricao());
        unidadeModalidade.setCapacidadePadrao(form.getCapacidadePadrao());
        unidadeModalidade.setAtivo(form.getAtivo() != null ? form.getAtivo() : Boolean.TRUE);
    }

    @Transactional
    public void inativar(Long idUnidadeModalidade) {
        UnidadeModalidade unidadeModalidade = unidadeModalidadeRepository.findById(idUnidadeModalidade)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo de modalidade não encontrado"));

        unidadeModalidade.setAtivo(Boolean.FALSE);
    }

    @Transactional(readOnly = true)
    public List<UnidadeModalidadeListagemDto> listar(Long idUnidade) {
        return unidadeModalidadeRepository.listarPorUnidade(idUnidade)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private UnidadeModalidadeListagemDto toDto(UnidadeModalidade unidadeModalidade) {
        Modalidade modalidade = unidadeModalidade.getModalidade();

        return new UnidadeModalidadeListagemDto(
                String.valueOf(unidadeModalidade.getId()),
                String.valueOf(unidadeModalidade.getUnidade().getId()),
                String.valueOf(modalidade.getId()),
                modalidade.getNome(),
                modalidade.getDescricao(),
                modalidade.getAtivo(),
                unidadeModalidade.getDescricao(),
                unidadeModalidade.getCapacidadePadrao(),
                unidadeModalidade.getAtivo()
        );
    }
}
