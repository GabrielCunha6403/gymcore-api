package br.gymcore.entities;

import br.gymcore.enums.FormaPagamentoCodigo;
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
@Table(name = "forma_pagamento")
public class FormaPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_forma_pagamento")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "codigo", length = 40, unique = true)
    private FormaPagamentoCodigo codigo;

    @Column(name = "descricao", length = 100)
    private String descricao;

    @Column(name = "permite_recorrencia")
    private Boolean permiteRecorrencia;

    @Column(name = "ativo")
    private Boolean ativo;
}
