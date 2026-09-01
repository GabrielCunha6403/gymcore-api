package br.gymcore.dtos;

import br.gymcore.enums.MensalidadeStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MensalidadeListagemDto(
        Long idMensalidade,
        LocalDate competencia,
        LocalDate dataVencimento,
        BigDecimal valorOriginal,
        BigDecimal valorDesconto,
        BigDecimal multa,
        BigDecimal juros,
        BigDecimal valorTotal,
        MensalidadeStatus status
) {
}
