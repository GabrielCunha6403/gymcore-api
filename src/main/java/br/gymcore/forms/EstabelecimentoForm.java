package br.gymcore.forms;

import br.gymcore.enums.EstabelecimentoStatus;
import br.gymcore.enums.TipoEstabelecimentoCodigo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EstabelecimentoForm {

    @NotBlank
    @Size(max = 150)
    private String nome;

    @NotBlank
    @Size(max = 180)
    private String razaoSocial;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\(\\d{2}\\) \\d{4,5}-\\d{4}$")
    private String telefone;

    @Size(max = 200)
    private String site;

    @Size(max = 500)
    private String logoUrl;

    @NotNull
    private TipoEstabelecimentoCodigo tipo;

    private EstabelecimentoStatus status;

    private Boolean ativo;
}
