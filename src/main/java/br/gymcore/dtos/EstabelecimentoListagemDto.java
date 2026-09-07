package br.gymcore.dtos;

import br.gymcore.enums.EstabelecimentoStatus;
import br.gymcore.enums.TipoEstabelecimentoCodigo;

public record EstabelecimentoListagemDto(
        String id,
        String nomeFantasia,
        String razaoSocial,
        String imagemUrl,
        String cnpj,
        String email,
        String telefone,
        String site,
        TipoEstabelecimentoCodigo tipo,
        EstabelecimentoStatus status,
        Boolean ativo,
        int quantidadeUnidades
) {
}
