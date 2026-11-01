package br.gymcore.dtos;

import br.gymcore.enums.TipoPendenciaInativacao;
import java.util.List;

public record VerificacaoInativacaoDto(
        boolean podeInativar,
        boolean exigeCascata,
        List<PendenciaInativacaoDto> pendencias
) {

    public static VerificacaoInativacaoDto of(List<PendenciaInativacaoDto> pendencias) {
        List<PendenciaInativacaoDto> existentes = pendencias.stream()
                .filter(pendencia -> pendencia.quantidade() > 0)
                .toList();

        return new VerificacaoInativacaoDto(
                existentes.stream().noneMatch(pendencia -> pendencia.tipo() == TipoPendenciaInativacao.BLOQUEANTE),
                existentes.stream().anyMatch(pendencia -> pendencia.tipo() == TipoPendenciaInativacao.CASCATA),
                existentes
        );
    }
}
