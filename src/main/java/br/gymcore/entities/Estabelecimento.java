package br.gymcore.entities;

import br.gymcore.enums.EstabelecimentoStatus;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "estabelecimento")
public class Estabelecimento extends UserAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_estabelecimento")
    private Long id;

    @Column(name = "nome", length = 150)
    private String nome;

    @Column(name = "razao_social", length = 180)
    private String razaoSocial;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "telefone", length = 30)
    private String telefone;

    @Column(name = "site", length = 200)
    private String site;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_estabelecimento")
    private TipoEstabelecimento tipoEstabelecimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private EstabelecimentoStatus status;

    @Column(name = "ativo")
    private Boolean ativo;
}
