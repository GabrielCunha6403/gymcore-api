package br.gymcore.dtos;

import br.gymcore.enums.EstabelecimentoStatus;

public record EstabelecimentoListagemDto(
        String id,
        String nomeFantasia,
        String razaoSocial,
        String imagemUrl,
        String cnpj,
        String email,
        String telefone,
        String site,
        EstabelecimentoStatus status,
        int quantidadeUnidades
) {
}
