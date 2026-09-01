package br.gymcore.dtos;

public record PlanoListagemDto(
        String id,
        String estabelecimentoId,
        String nome,
        String descricao,
        Boolean ativo
) {
}
