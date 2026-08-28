package br.gymcore.forms;

import jakarta.validation.Valid;
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
public class UnidadeForm {

    @NotNull
    private Long idEstabelecimento;

    @NotBlank
    @Size(max = 150)
    private String nome;

    @NotBlank
    @Pattern(regexp = "^(\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2})$")
    private String cnpj;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\(\\d{2}\\) \\d{4,5}-\\d{4}$")
    private String telefone;

    @Valid
    @NotNull
    private Endereco endereco;

    private Boolean ativo;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Endereco {

        @NotBlank
        @Pattern(regexp = "^(\\d{8}|\\d{5}-\\d{3})$")
        private String cep;

        @NotBlank
        @Size(max = 200)
        private String logradouro;

        @NotBlank
        @Size(max = 30)
        private String numero;

        @Size(max = 100)
        private String complemento;

        @NotBlank
        @Size(max = 100)
        private String bairro;

        @NotBlank
        @Size(max = 100)
        private String cidade;

        @NotBlank
        @Size(min = 2, max = 2)
        private String uf;
    }
}
