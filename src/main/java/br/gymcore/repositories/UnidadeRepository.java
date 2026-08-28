package br.gymcore.repositories;

import br.gymcore.entities.Unidade;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UnidadeRepository extends JpaRepository<Unidade, Long> {

    @Query("""
            select u
            from Unidade u
            join fetch u.estabelecimento e
            where e.id in :estabelecimentoIds
            order by e.id, u.id
            """)
    List<Unidade> findAllByEstabelecimento_IdIn(
            @Param("estabelecimentoIds") List<Long> estabelecimentoIds
    );

    @Query("""
            select u
            from Unidade u
            join fetch u.estabelecimento e
            left join fetch e.tipoEstabelecimento
            where (:idEstabelecimento is null or e.id = :idEstabelecimento)
              and (
                   :busca = ''
                   or lower(u.nome) like concat('%', :busca, '%')
                   or (
                        function('regexp_replace', :busca, '[^0-9]', '', 'g') <> ''
                        and u.cnpj like concat('%', function('regexp_replace', :busca, '[^0-9]', '', 'g'), '%')
                   )
              )
            order by e.id, u.id
            """)
    List<Unidade> listarComFiltros(
            @Param("idEstabelecimento") Long idEstabelecimento,
            @Param("busca") String busca
    );
}
