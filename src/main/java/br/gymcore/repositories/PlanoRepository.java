package br.gymcore.repositories;

import br.gymcore.entities.Plano;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlanoRepository extends JpaRepository<Plano, Long> {

    @Query("""
            select p
            from Plano p
            join fetch p.estabelecimento e
            where (:idEstabelecimento is null or e.id = :idEstabelecimento)
              and (:busca = '' or lower(p.nome) like concat('%', :busca, '%'))
            order by e.id, p.nome
            """)
    List<Plano> listarComFiltros(
            @Param("idEstabelecimento") Long idEstabelecimento,
            @Param("busca") String busca
    );
}
