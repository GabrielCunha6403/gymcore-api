package br.gymcore.repositories;

import br.gymcore.entities.Estabelecimento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long> {

    @Query("""
            select e
            from Estabelecimento e
            left join fetch e.tipoEstabelecimento
            where :busca = ''
               or lower(e.nome) like concat('%', :busca, '%')
               or (
                    function('regexp_replace', :busca, '[^0-9]', '', 'g') <> ''
                    and exists (
                        select 1
                        from Unidade u
                        where u.estabelecimento = e
                          and u.cnpj like concat('%', function('regexp_replace', :busca, '[^0-9]', '', 'g'), '%')
                    )
               )
            order by lower(e.nome), e.id
            """)
    List<Estabelecimento> listarComFiltros(
            @Param("busca") String busca
    );
}
