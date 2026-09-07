package br.gymcore.dtos;

import java.time.LocalDate;
import java.util.List;

public record AlunoTurmaListagemDto(
        String idAlunoTurma,
        String idTurma,
        String turmaNome,
        String modalidadeNome,
        String professorNome,
        List<TurmaHorarioDto> horarios,
        LocalDate dataInicio,
        LocalDate dataFim,
        Boolean ativo
) {
}
