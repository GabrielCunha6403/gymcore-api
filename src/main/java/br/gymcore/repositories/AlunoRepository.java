package br.gymcore.repositories;

import br.gymcore.entities.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    @Query("""
            select a
            from Aluno a
            join a.pessoa pessoa
            where lower(pessoa.nome) like concat('%', :busca, '%')
               or pessoa.cpf like concat('%', :busca, '%')
               or exists (
                    select 1
                    from Matricula m
                    where m.aluno = a
                      and str(m.id) like concat('%', :busca, '%')
              )
               or exists (
                    select 1
                    from Matricula m
                    where m.aluno = a
                      and lower(m.planoUnidade.plano.nome) like concat('%', :busca, '%')
              )
               or exists (
                    select 1
                    from Matricula m
                    where m.aluno = a
                      and lower(m.planoUnidade.unidade.nome) like concat('%', :busca, '%')
              )
               or exists (
                    select 1
                    from Matricula m
                    join PlanoUnidadeModalidade pum on pum.planoUnidade = m.planoUnidade
                    join pum.unidadeModalidade um
                    join um.modalidade modalidade
                    where m.aluno = a
                      and lower(modalidade.nome) like concat('%', :busca, '%')
              )
            """)
    Page<Aluno> listarComFiltros(
            @Param("busca") String busca,
            Pageable pageable
    );
}
