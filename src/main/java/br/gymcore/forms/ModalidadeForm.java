package br.gymcore.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModalidadeForm {

    @NotNull
    private Long idEstabelecimento;

    @NotBlank
    @Size(max = 120)
    private String nome;

    @Size(max = 1000)
    private String descricao;

    private Boolean ativo;
}
