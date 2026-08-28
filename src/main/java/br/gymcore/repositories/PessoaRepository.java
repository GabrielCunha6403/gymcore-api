package br.gymcore.repositories;

import br.gymcore.entities.Pessoa;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, UUID> {
}
