package br.gymcore.dtos;

public record ModalidadeListagemDto(
        String id,
        String estabelecimentoId,
        String nome,
        String descricao,
        Boolean ativo
) {
}
