package br.gymcore.forms;

import br.gymcore.enums.TipoCobrancaCodigo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlanoUnidadeForm {

    @NotNull
    private Long idUnidade;

    @NotNull
    private Long idPlano;

    @NotBlank
    @Size(max = 120)
    private String nomeExibicao;

    @Size(max = 1000)
    private String descricao;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal valor;

    @Min(1)
    private Integer duracaoMeses;

    @NotNull
    private TipoCobrancaCodigo tipoCobranca;

    @DecimalMin(value = "0.0")
    private BigDecimal taxaAdesao;

    @Min(1)
    @Max(31)
    private Integer diaVencimentoPadrao;

    private Boolean ativo;

    private List<Long> modalidades;
}
