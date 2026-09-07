package br.gymcore.forms;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlunoTurmaForm {

    @NotNull
    private Long idMatricula;

    @NotNull
    private Long idTurma;

    private LocalDate dataInicio;

    private Boolean ativo;
}
