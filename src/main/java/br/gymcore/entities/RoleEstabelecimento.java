package br.gymcore.entities;

import br.gymcore.enums.RoleEstabelecimentoCodigo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "role_estabelecimento")
public class RoleEstabelecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_role_estabelecimento")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "codigo", length = 40, unique = true)
    private RoleEstabelecimentoCodigo codigo;

    @Column(name = "descricao", length = 100)
    private String descricao;

    @Column(name = "ativo")
    private Boolean ativo;
}
