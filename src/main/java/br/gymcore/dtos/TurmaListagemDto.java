package br.gymcore.dtos;

import java.util.List;

public record TurmaListagemDto(
        String id,
        String unidadeId,
        String unidadeModalidadeId,
        String modalidadeNome,
        String professorId,
        String professorNome,
        String nome,
        Integer capacidade,
        Integer matriculados,
        Boolean ativo,
        List<TurmaHorarioDto> horarios
) {
}
