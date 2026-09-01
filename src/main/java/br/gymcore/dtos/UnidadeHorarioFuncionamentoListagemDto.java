package br.gymcore.dtos;

import java.time.LocalTime;

public record UnidadeHorarioFuncionamentoListagemDto(
        Integer diaSemana,
        LocalTime horaAbertura,
        LocalTime horaFechamento
) {
}
