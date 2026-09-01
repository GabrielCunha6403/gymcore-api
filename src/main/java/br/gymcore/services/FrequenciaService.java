package br.gymcore.services;

import br.gymcore.dtos.FrequenciaListagemDto;
import br.gymcore.entities.Frequencia;
import br.gymcore.entities.Unidade;
import br.gymcore.repositories.FrequenciaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FrequenciaService {

    private final FrequenciaRepository frequenciaRepository;

    @Transactional(readOnly = true)
    public List<FrequenciaListagemDto> listarPorAluno(Long idAluno) {
        return frequenciaRepository.findTop10ByAluno_IdOrderByDataHoraEntradaDesc(idAluno)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private FrequenciaListagemDto toDto(Frequencia frequencia) {
        Unidade unidade = frequencia.getUnidade();

        return new FrequenciaListagemDto(
                frequencia.getId(),
                unidade != null ? unidade.getNome() : null,
                frequencia.getDataHoraEntrada(),
                frequencia.getDataHoraSaida()
        );
    }
}
