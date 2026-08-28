package br.gymcore.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "plano_unidade")
public class PlanoUnidade extends UserAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_plano_unidade")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plano")
    private Plano plano;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidade")
    private Unidade unidade;

    @Column(name = "nome_exibicao", length = 120)
    private String nomeExibicao;

    @Column(name = "descricao", columnDefinition = "text")
    private String descricao;

    @Column(name = "valor", precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(name = "duracao_meses")
    private Integer duracaoMeses;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_cobranca")
    private TipoCobranca tipoCobranca;

    @Column(name = "taxa_adesao", precision = 12, scale = 2)
    private BigDecimal taxaAdesao;

    @Column(name = "dia_vencimento_padrao")
    private Integer diaVencimentoPadrao;

    @Column(name = "ativo")
    private Boolean ativo;
}
