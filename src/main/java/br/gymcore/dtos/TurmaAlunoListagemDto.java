package br.gymcore.dtos;

import java.time.LocalDate;

public record TurmaAlunoListagemDto(
        String idAlunoTurma,
        String idAluno,
        String alunoNome,
        LocalDate dataInicio,
        LocalDate dataFim,
        Boolean ativo
) {
}
