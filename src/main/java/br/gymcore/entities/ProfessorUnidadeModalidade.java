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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "professor_unidade_modalidade")
public class ProfessorUnidadeModalidade extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_professor_unidade_modalidade")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_professor_unidade")
    private ProfessorUnidade professorUnidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidade_modalidade")
    private UnidadeModalidade unidadeModalidade;

    @Column(name = "ativo")
    private Boolean ativo;
}
