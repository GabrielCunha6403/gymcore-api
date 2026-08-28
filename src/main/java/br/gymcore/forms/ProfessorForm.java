package br.gymcore.forms;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfessorForm {

    @Valid
    @NotNull
    private DadosPessoais dadosPessoais;

    @Valid
    @NotNull
    private Endereco endereco;

    @Valid
    @NotNull
    private Profissional profissional;

    @Valid
    @NotNull
    private Atuacao atuacao;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DadosPessoais {

        @NotBlank
        @Size(max = 150)
        private String nome;

        @NotBlank
        @Pattern(regexp = "^(\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})$")
        private String cpf;

        @NotNull
        private LocalDate dataNascimento;

        @Size(max = 30)
        private String sexo;

        @NotBlank
        @Email
        @Size(max = 150)
        private String email;

        @NotBlank
        @Pattern(regexp = "^\\(\\d{2}\\) \\d{4,5}-\\d{4}$")
        private String telefone;
    }

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
        @Pattern(regexp = "^\\d+$")
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

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Profissional {

        @NotBlank
        @Size(max = 50)
        private String registroProfissional;

        private String observacoes;

        @NotNull
        private Boolean ativo;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Atuacao {

        @NotNull
        private Long estabelecimentoId;

        @NotNull
        private Long unidadeId;

        @NotBlank
        @Size(max = 50)
        private String codigoInterno;

        private List<Long> modalidades;

        @NotNull
        private Boolean ativo;
    }
}
