package br.gymcore.dtos;

import java.util.List;

public record ModalidadeGeralListagemDto(
        Long idModalidade,
        String nome,
        String descricao,
        Boolean ativo,
        String estabelecimentoNome,
        List<String> unidades,
        List<String> professores
) {
}
