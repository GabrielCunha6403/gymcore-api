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
            join fetch pu.unidade u
            join fetch u.estabelecimento e
            left join fetch pu.tipoCobranca tc
            where pu.unidade.id = :idUnidade
            order by p.nome
            """)
    List<PlanoUnidade> listarPorUnidade(@Param("idUnidade") Long idUnidade);

    @Query("""
            select pu
            from PlanoUnidade pu
            join fetch pu.plano p
            join fetch pu.unidade u
            join fetch u.estabelecimento e
            left join fetch pu.tipoCobranca tc
            where :busca = ''
               or lower(p.nome) like concat('%', :busca, '%')
               or lower(pu.nomeExibicao) like concat('%', :busca, '%')
               or lower(u.nome) like concat('%', :busca, '%')
               or lower(e.nome) like concat('%', :busca, '%')
            order by e.nome, u.nome, p.nome
            """)
    List<PlanoUnidade> listarGeral(@Param("busca") String busca);
}
