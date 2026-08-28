package br.gymcore.entities;

import br.gymcore.enums.MensalidadeStatus;
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
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mensalidade")
public class Mensalidade extends UserAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_mensalidade")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_matricula")
    private Matricula matricula;

    @Column(name = "competencia")
    private LocalDate competencia;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(name = "valor_original", precision = 12, scale = 2)
    private BigDecimal valorOriginal;

    @Column(name = "valor_desconto", precision = 12, scale = 2)
    private BigDecimal valorDesconto;

    @Column(name = "multa", precision = 12, scale = 2)
    private BigDecimal multa;

    @Column(name = "juros", precision = 12, scale = 2)
    private BigDecimal juros;

    @Column(name = "valor_total", precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private MensalidadeStatus status;
}
