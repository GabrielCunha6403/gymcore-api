package br.gymcore.repositories;

import br.gymcore.entities.TipoCobranca;
import br.gymcore.enums.TipoCobrancaCodigo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoCobrancaRepository extends JpaRepository<TipoCobranca, Long> {

    Optional<TipoCobranca> findByCodigoAndAtivoTrue(TipoCobrancaCodigo codigo);
}
