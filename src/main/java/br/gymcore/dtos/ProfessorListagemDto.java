package br.gymcore.dtos;

import java.util.List;

public record ProfessorListagemDto(
        Long idProfessor,
        String nome,
        String cpf,
        String email,
        String contato,
        List<String> unidades,
        List<String> modalidades,
        Boolean status
) {
}
