package br.gymcore.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class PlanoUnidadeModalidadeId implements Serializable {

    @Column(name = "id_plano_unidade")
    private Long planoUnidadeId;

    @Column(name = "id_unidade_modalidade")
    private Long unidadeModalidadeId;
}
