package br.gymcore.entities;

import br.gymcore.enums.TipoDescontoCodigo;
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
@Table(name = "tipo_desconto")
public class TipoDesconto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_tipo_desconto")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "codigo", length = 30, unique = true)
    private TipoDescontoCodigo codigo;

    @Column(name = "descricao", length = 100)
    private String descricao;

    @Column(name = "ativo")
    private Boolean ativo;
}
