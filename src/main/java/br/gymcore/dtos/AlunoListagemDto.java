package br.gymcore.dtos;

import br.gymcore.enums.MatriculaStatus;
import java.time.LocalDate;
import java.util.List;

public record AlunoListagemDto(
        Long idAluno,
        String nome,
        String cpf,
        String email,
        String contato,
        Boolean ativo,
        List<String> unidades,
        String planoAtual,
        List<String> modalidades,
        MatriculaResumoDto matricula
) {

    public record MatriculaResumoDto(
            Long idMatricula,
            LocalDate dataInicio,
            Integer diaVencimento,
            MatriculaStatus status
    ) {
    }
}
