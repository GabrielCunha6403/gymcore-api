package br.gymcore.dtos;

public record UnidadeModalidadeListagemDto(
        String id,
        String unidadeId,
        String modalidadeId,
        String modalidadeNome,
        String modalidadeDescricao,
        Boolean modalidadeAtivo,
        String descricao,
        Integer capacidadePadrao,
        Boolean ativo
) {
}
