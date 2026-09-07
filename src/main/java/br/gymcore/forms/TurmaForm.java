package br.gymcore.forms;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TurmaForm {

    @NotNull
    private Long idUnidadeModalidade;

    @NotNull
    private Long idProfessor;

    @NotBlank
    @Size(max = 120)
    private String nome;

    @NotNull
    @Min(1)
    private Integer capacidade;

    private Boolean ativo;

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

        @NotNull
        private LocalTime horaInicio;

        @NotNull
        private LocalTime horaFim;
    }
}
