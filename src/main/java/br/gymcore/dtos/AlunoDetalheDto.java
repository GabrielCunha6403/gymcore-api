package br.gymcore.dtos;

import br.gymcore.enums.MatriculaStatus;
import java.time.LocalDate;
import java.util.List;

public record AlunoDetalheDto(
        Long idAluno,
        String nome,
        String cpf,
        String email,
        String contato,
        LocalDate dataNascimento,
        String sexo,
        EnderecoDto endereco,
        Boolean ativo,
        List<UnidadeVinculadaDto> unidades,
        List<String> modalidades,
        MatriculaDetalheDto matricula
) {

    public record EnderecoDto(
            String cep,
            String logradouro,
            String numero,
            String complemento,
            String bairro,
            String cidade,
            String uf
    ) {
    }

    public record UnidadeVinculadaDto(
            String estabelecimento,
            String unidade
    ) {
    }

    public record MatriculaDetalheDto(
            Long idMatricula,
            Long planoUnidadeId,
            String unidade,
            String plano,
            LocalDate dataInicio,
            LocalDate dataFim,
            Integer diaVencimento,
            MatriculaStatus status,
            String motivoCancelamento
    ) {
    }
}
