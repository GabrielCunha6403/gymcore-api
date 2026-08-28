package br.gymcore.entities;

import br.gymcore.enums.CondicaoDescontoCodigo;
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
@Table(name = "condicao_desconto")
public class CondicaoDesconto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_condicao_desconto")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "codigo", length = 50, unique = true)
    private CondicaoDescontoCodigo codigo;

    @Column(name = "descricao", length = 150)
    private String descricao;

    @Column(name = "ativo")
    private Boolean ativo;
}
