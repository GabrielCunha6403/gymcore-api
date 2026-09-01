package br.gymcore.forms;

import br.gymcore.enums.MatriculaStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlunoForm {

    @Valid
    @NotNull
    private DadosPessoais dadosPessoais;

    @Valid
    @NotNull
    private Endereco endereco;

    @Valid
    @NotNull
    private Matricula matricula;

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

        @NotNull
        private Boolean ativo;
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
    public static class Matricula {

        @NotNull
        private Long planoUnidadeId;

        @NotNull
        private LocalDate dataInicio;

        private LocalDate dataFim;

        @NotNull
        @Min(1)
        @Max(31)
        private Integer diaVencimento;

        @NotNull
        private MatriculaStatus status;

        private String motivoCancelamento;
    }
}
