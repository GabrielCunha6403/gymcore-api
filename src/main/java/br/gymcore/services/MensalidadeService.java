package br.gymcore.services;

import br.gymcore.dtos.MensalidadeListagemDto;
import br.gymcore.entities.Mensalidade;
import br.gymcore.repositories.MensalidadeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MensalidadeService {

    private final MensalidadeRepository mensalidadeRepository;

    @Transactional(readOnly = true)
    public List<MensalidadeListagemDto> listarPorAluno(Long idAluno) {
        return mensalidadeRepository.findAllByMatricula_Aluno_IdOrderByCompetenciaDesc(idAluno)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private MensalidadeListagemDto toDto(Mensalidade mensalidade) {
        return new MensalidadeListagemDto(
                mensalidade.getId(),
                mensalidade.getCompetencia(),
                mensalidade.getDataVencimento(),
                mensalidade.getValorOriginal(),
                mensalidade.getValorDesconto(),
                mensalidade.getMulta(),
                mensalidade.getJuros(),
                mensalidade.getValorTotal(),
                mensalidade.getStatus()
        );
    }
}
