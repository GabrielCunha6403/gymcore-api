package br.gymcore.dtos;

import java.time.LocalDateTime;

public record FrequenciaListagemDto(
        Long idFrequencia,
        String unidade,
        LocalDateTime dataHoraEntrada,
        LocalDateTime dataHoraSaida
) {
}
