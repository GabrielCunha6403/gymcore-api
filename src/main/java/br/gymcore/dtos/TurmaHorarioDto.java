package br.gymcore.dtos;

import java.time.LocalTime;

public record TurmaHorarioDto(
        Integer diaSemana,
        LocalTime horaInicio,
        LocalTime horaFim
) {
}
