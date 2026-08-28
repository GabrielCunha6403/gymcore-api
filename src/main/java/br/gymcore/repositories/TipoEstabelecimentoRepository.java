package br.gymcore.repositories;

import br.gymcore.entities.TipoEstabelecimento;
import br.gymcore.enums.TipoEstabelecimentoCodigo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoEstabelecimentoRepository extends JpaRepository<TipoEstabelecimento, Long> {

    Optional<TipoEstabelecimento> findByCodigoAndAtivoTrue(TipoEstabelecimentoCodigo codigo);
}
