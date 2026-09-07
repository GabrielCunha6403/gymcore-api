package br.gymcore.dtos;

import br.gymcore.enums.TipoCobrancaCodigo;
import java.math.BigDecimal;
import java.util.List;

public record PlanoUnidadeDetalheDto(
        String id,
        String unidadeId,
        String planoId,
        String planoNome,
        String nomeExibicao,
        String descricao,
        BigDecimal valor,
        Integer duracaoMeses,
        TipoCobrancaCodigo tipoCobranca,
        BigDecimal taxaAdesao,
        Integer diaVencimentoPadrao,
        Boolean ativo,
        List<String> modalidadesIds
) {
}
