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
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "unidade_horario_funcionamento")
public class UnidadeHorarioFuncionamento extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_unidade_horario_funcionamento")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidade")
    private Unidade unidade;

    @Column(name = "dia_semana")
    private Integer diaSemana;

    @Column(name = "hora_abertura")
    private LocalTime horaAbertura;

    @Column(name = "hora_fechamento")
    private LocalTime horaFechamento;
}
