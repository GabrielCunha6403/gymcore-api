package br.gymcore.entities;

import br.gymcore.enums.AcaoAuditoria;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "unidade_auditoria")
public class UnidadeAuditoria extends CreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_unidade_auditoria")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidade")
    private Unidade unidade;

    @Column(name = "id_usuario")
    private UUID idUsuario;

    @Column(name = "tipo_registro", length = 100)
    private String tipoRegistro;

    @Column(name = "id_registro")
    private Long idRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "acao", length = 50)
    private AcaoAuditoria acao;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dados_anteriores", columnDefinition = "jsonb")
    private Map<String, Object> dadosAnteriores;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dados_novos", columnDefinition = "jsonb")
    private Map<String, Object> dadosNovos;
}
