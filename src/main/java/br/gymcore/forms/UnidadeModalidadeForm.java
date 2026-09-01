package br.gymcore.forms;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UnidadeModalidadeForm {

    @NotNull
    private Long idUnidade;

    @NotNull
    private Long idModalidade;

    @Size(max = 1000)
    private String descricao;

    @Min(1)
    private Integer capacidadePadrao;

    private Boolean ativo;
}
