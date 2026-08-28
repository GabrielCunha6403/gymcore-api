package br.gymcore.dtos;

import br.gymcore.enums.EstabelecimentoStatus;
import br.gymcore.enums.TipoEstabelecimentoCodigo;

public record UnidadeListagemDto(
        String id,
        String estabelecimentoId,
        String nome,
        String imagemUrl,
        String cnpj,
        TipoEstabelecimentoCodigo tipo,
        String email,
        String telefone,
        EnderecoDto endereco,
        EstabelecimentoStatus status,
        Boolean matriz
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
}
