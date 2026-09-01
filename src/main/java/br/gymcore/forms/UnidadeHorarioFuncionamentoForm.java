package br.gymcore.forms;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UnidadeHorarioFuncionamentoForm {

    @NotNull
    private Long idUnidade;

    @NotEmpty
    private List<@Valid Horario> horarios;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Horario {

        @NotNull
        @Min(1)
        @Max(7)
        private Integer diaSemana;

        private LocalTime horaAbertura;

        private LocalTime horaFechamento;
    }
}
