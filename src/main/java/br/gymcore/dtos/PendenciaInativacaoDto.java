package br.gymcore.dtos;

import br.gymcore.enums.TipoPendenciaInativacao;
import java.util.List;
import java.util.function.Function;

public record PendenciaInativacaoDto(
        TipoPendenciaInativacao tipo,
        String entidade,
        String descricao,
        int quantidade,
        List<ItemPendenciaInativacaoDto> itens
) {

    private static final int LIMITE_ITENS = 10;

    public static <T> PendenciaInativacaoDto of(
            TipoPendenciaInativacao tipo,
            String entidade,
            String descricao,
            List<T> registros,
            Function<T, ItemPendenciaInativacaoDto> toItem
    ) {
        return new PendenciaInativacaoDto(
                tipo,
                entidade,
                descricao,
                registros.size(),
                registros.stream().limit(LIMITE_ITENS).map(toItem).toList()
        );
    }
}
