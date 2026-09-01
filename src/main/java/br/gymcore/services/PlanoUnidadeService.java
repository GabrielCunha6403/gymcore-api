package br.gymcore.services;

import br.gymcore.dtos.PlanoUnidadeListagemDto;
import br.gymcore.entities.Plano;
import br.gymcore.entities.PlanoUnidade;
import br.gymcore.entities.TipoCobranca;
import br.gymcore.entities.Unidade;
import br.gymcore.forms.PlanoUnidadeForm;
import br.gymcore.repositories.PlanoRepository;
import br.gymcore.repositories.PlanoUnidadeRepository;
import br.gymcore.repositories.TipoCobrancaRepository;
import br.gymcore.repositories.UnidadeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlanoUnidadeService {

    private final UnidadeRepository unidadeRepository;
    private final PlanoRepository planoRepository;
    private final TipoCobrancaRepository tipoCobrancaRepository;
    private final PlanoUnidadeRepository planoUnidadeRepository;

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

        return planoUnidadeRepository.save(planoUnidade).getId();
    }

    @Transactional(readOnly = true)
    public List<PlanoUnidadeListagemDto> listar(Long idUnidade) {
        return planoUnidadeRepository.listarPorUnidade(idUnidade)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private PlanoUnidadeListagemDto toDto(PlanoUnidade planoUnidade) {
        Plano plano = planoUnidade.getPlano();
        TipoCobranca tipoCobranca = planoUnidade.getTipoCobranca();

        return new PlanoUnidadeListagemDto(
                String.valueOf(planoUnidade.getId()),
                String.valueOf(planoUnidade.getUnidade().getId()),
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
                planoUnidade.getAtivo()
        );
    }
}
