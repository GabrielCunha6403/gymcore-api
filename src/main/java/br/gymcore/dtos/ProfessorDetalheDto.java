package br.gymcore.dtos;

import br.gymcore.enums.ProfessorStatus;
import java.time.LocalDate;
import java.util.List;

public record ProfessorDetalheDto(
        Long idProfessor,
        String nome,
        String cpf,
        String email,
        String contato,
        LocalDate dataNascimento,
        String sexo,
        EnderecoDto endereco,
        String registroProfissional,
        String observacoes,
        String codigoInterno,
        Boolean ativoAtuacao,
        List<UnidadeVinculadaDto> unidades,
        List<String> modalidades,
        ProfessorStatus status
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
}
