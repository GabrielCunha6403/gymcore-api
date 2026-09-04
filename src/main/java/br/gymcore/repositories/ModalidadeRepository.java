package br.gymcore.repositories;

import br.gymcore.entities.Modalidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ModalidadeRepository extends JpaRepository<Modalidade, Long> {

    @Query("""
            select m
            from Modalidade m
            join fetch m.estabelecimento e
            where (:idEstabelecimento is null or e.id = :idEstabelecimento)
              and (:busca = '' or lower(m.nome) like concat('%', :busca, '%'))
            order by e.id, m.nome
            """)
    List<Modalidade> listarComFiltros(
            @Param("idEstabelecimento") Long idEstabelecimento,
            @Param("busca") String busca
    );

    @Query(value = """
            select m
            from Modalidade m
            join fetch m.estabelecimento e
            where (:busca = '' or lower(m.nome) like concat('%', :busca, '%')
                                or lower(e.nome) like concat('%', :busca, '%'))
            """,
            countQuery = """
            select count(m)
            from Modalidade m
            join m.estabelecimento e
            where (:busca = '' or lower(m.nome) like concat('%', :busca, '%')
                                or lower(e.nome) like concat('%', :busca, '%'))
            """)
    Page<Modalidade> listarGeralComFiltros(@Param("busca") String busca, Pageable pageable);
}
