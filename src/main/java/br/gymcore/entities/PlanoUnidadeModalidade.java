package br.gymcore.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "plano_unidade_modalidade")
public class PlanoUnidadeModalidade extends CreatedAtEntity {

    @EmbeddedId
    private PlanoUnidadeModalidadeId id;

    @MapsId("planoUnidadeId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plano_unidade")
    private PlanoUnidade planoUnidade;

    @MapsId("unidadeModalidadeId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidade_modalidade")
    private UnidadeModalidade unidadeModalidade;
}
