package br.gymcore.entities;

import br.gymcore.enums.PagamentoStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "pagamento")
public class Pagamento extends UserAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_pagamento")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mensalidade")
    private Mensalidade mensalidade;

    @Column(name = "valor_pago", precision = 12, scale = 2)
    private BigDecimal valorPago;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_forma_pagamento")
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private PagamentoStatus status;

    @Column(name = "referencia_externa", length = 200)
    private String referenciaExterna;

    @Column(name = "observacao", columnDefinition = "text")
    private String observacao;
}
