package br.gymcore.repositories;

import br.gymcore.entities.Professor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    @Query("""
            select p
            from Professor p
            join p.pessoa pessoa
            where lower(pessoa.nome) like concat('%', :busca, '%')
               or pessoa.cpf like concat('%', :busca, '%')
               or exists (
                    select 1
                    from ProfessorUnidade puEstabelecimento
                    where puEstabelecimento.professor = p
                      and lower(puEstabelecimento.unidade.estabelecimento.nome) like concat('%', :busca, '%')
              )
               or exists (
                    select 1
                    from ProfessorUnidade puUnidade
                    where puUnidade.professor = p
                      and lower(puUnidade.unidade.nome) like concat('%', :busca, '%')
              )
               or exists (
                    select 1
                    from ProfessorUnidadeModalidade pum
                    where pum.professorUnidade.professor = p
                      and lower(pum.unidadeModalidade.modalidade.nome) like concat('%', :busca, '%')
              )
            """)
    Page<Professor> listarComFiltros(
            @Param("busca") String busca,
            Pageable pageable
    );
}
