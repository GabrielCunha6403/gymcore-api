package br.gymcore.entities;

import br.gymcore.enums.TipoAplicacaoDescontoCodigo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tipo_aplicacao_desconto")
public class TipoAplicacaoDesconto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_tipo_aplicacao_desconto")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "codigo", length = 30, unique = true)
    private TipoAplicacaoDescontoCodigo codigo;

    @Column(name = "descricao", length = 150)
    private String descricao;

    @Column(name = "ativo")
    private Boolean ativo;
}
