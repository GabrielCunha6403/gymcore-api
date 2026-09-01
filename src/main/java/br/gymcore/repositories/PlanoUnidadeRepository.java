package br.gymcore.repositories;

import br.gymcore.entities.PlanoUnidade;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlanoUnidadeRepository extends JpaRepository<PlanoUnidade, Long> {

    boolean existsByUnidade_IdAndPlano_Id(Long unidadeId, Long planoId);

    @Query("""
            select pu
            from PlanoUnidade pu
            join fetch pu.plano p
            left join fetch pu.tipoCobranca tc
            where pu.unidade.id = :idUnidade
            order by p.nome
            """)
    List<PlanoUnidade> listarPorUnidade(@Param("idUnidade") Long idUnidade);
}
