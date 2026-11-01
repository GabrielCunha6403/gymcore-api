package br.gymcore.services;

import br.gymcore.dtos.ItemPendenciaInativacaoDto;
import br.gymcore.dtos.PendenciaInativacaoDto;
import br.gymcore.dtos.UnidadeModalidadeListagemDto;
import br.gymcore.entities.Modalidade;
import br.gymcore.entities.PlanoUnidade;
import br.gymcore.entities.ProfessorUnidade;
import br.gymcore.entities.ProfessorUnidadeModalidade;
import br.gymcore.entities.Unidade;
import br.gymcore.entities.UnidadeModalidade;
import br.gymcore.enums.TipoPendenciaInativacao;
import br.gymcore.forms.UnidadeModalidadeForm;
import br.gymcore.repositories.ModalidadeRepository;
import br.gymcore.repositories.PlanoUnidadeModalidadeRepository;
import br.gymcore.repositories.ProfessorUnidadeModalidadeRepository;
import br.gymcore.repositories.TurmaRepository;
import br.gymcore.repositories.UnidadeModalidadeRepository;
import br.gymcore.repositories.UnidadeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnidadeModalidadeService {

    private final UnidadeRepository unidadeRepository;
    private final ModalidadeRepository modalidadeRepository;
    private final UnidadeModalidadeRepository unidadeModalidadeRepository;
    private final TurmaRepository turmaRepository;
    private final PlanoUnidadeModalidadeRepository planoUnidadeModalidadeRepository;
    private final ProfessorUnidadeModalidadeRepository professorUnidadeModalidadeRepository;

    @Transactional
    public Long vincular(UnidadeModalidadeForm form) {
        Unidade unidade = unidadeRepository.findById(form.getIdUnidade())
                .orElseThrow(() -> new EntityNotFoundException("Unidade não encontrada"));

        Modalidade modalidade = modalidadeRepository.findById(form.getIdModalidade())
                .orElseThrow(() -> new EntityNotFoundException("Modalidade não encontrada"));

        if (!Objects.equals(unidade.getEstabelecimento().getId(), modalidade.getEstabelecimento().getId())) {
            throw new IllegalArgumentException("Modalidade não pertence ao estabelecimento da unidade");
        }

        // Um vínculo desvinculado (inativo) é sobrescrito em vez de duplicar o par unidade/modalidade.
        UnidadeModalidade unidadeModalidade = unidadeModalidadeRepository
                .findByUnidade_IdAndModalidade_Id(unidade.getId(), modalidade.getId())
                .orElseGet(UnidadeModalidade::new);

        if (Boolean.TRUE.equals(unidadeModalidade.getAtivo())) {
            throw new IllegalStateException("Modalidade já vinculada a esta unidade");
        }

        unidadeModalidade.setUnidade(unidade);
        unidadeModalidade.setModalidade(modalidade);
        unidadeModalidade.setDescricao(form.getDescricao());
        unidadeModalidade.setCapacidadePadrao(form.getCapacidadePadrao());
        unidadeModalidade.setAtivo(form.getAtivo() != null ? form.getAtivo() : Boolean.TRUE);

        return unidadeModalidadeRepository.save(unidadeModalidade).getId();
    }

    @Transactional
    public void atualizar(Long idUnidadeModalidade, UnidadeModalidadeForm form) {
        UnidadeModalidade unidadeModalidade = unidadeModalidadeRepository.findById(idUnidadeModalidade)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo de modalidade não encontrado"));

        unidadeModalidade.setDescricao(form.getDescricao());
        unidadeModalidade.setCapacidadePadrao(form.getCapacidadePadrao());
        unidadeModalidade.setAtivo(form.getAtivo() != null ? form.getAtivo() : Boolean.TRUE);
    }

    @Transactional
    public void inativar(Long idUnidadeModalidade) {
        UnidadeModalidade unidadeModalidade = unidadeModalidadeRepository.findById(idUnidadeModalidade)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo de modalidade não encontrado"));

        unidadeModalidade.setAtivo(Boolean.FALSE);
    }

    /**
     * Pendências que impedem (ou acompanham em cadeia) a inativação das ofertas informadas.
     * Todas as consultas são restritas ao estabelecimento dono das ofertas.
     */
    @Transactional(readOnly = true)
    public List<PendenciaInativacaoDto> verificarPendenciasInativacao(
            Collection<UnidadeModalidade> ofertas,
            Long idEstabelecimento
    ) {
        if (ofertas.isEmpty()) {
            return List.of();
        }

        List<Long> idsOferta = ofertas.stream().map(UnidadeModalidade::getId).toList();

        return List.of(
                PendenciaInativacaoDto.of(
                        TipoPendenciaInativacao.BLOQUEANTE,
                        "turma",
                        "Turmas ativas desta modalidade. Inative-as ou altere a modalidade antes.",
                        turmaRepository.listarAtivasPorUnidadeModalidade(idsOferta, idEstabelecimento),
                        turma -> new ItemPendenciaInativacaoDto(
                                String.valueOf(turma.getId()),
                                turma.getNome() + " (" + turma.getUnidadeModalidade().getUnidade().getNome() + ")"
                        )
                ),
                PendenciaInativacaoDto.of(
                        TipoPendenciaInativacao.BLOQUEANTE,
                        "plano_unidade",
                        "Planos ativos que incluem esta modalidade. Remova a modalidade do plano ou inative a oferta do plano antes.",
                        planoUnidadeModalidadeRepository.listarComPlanoUnidadeAtivo(idsOferta, idEstabelecimento),
                        vinculo -> {
                            PlanoUnidade planoUnidade = vinculo.getPlanoUnidade();
                            return new ItemPendenciaInativacaoDto(
                                    String.valueOf(planoUnidade.getId()),
                                    planoUnidade.getNomeExibicao() + " (" + planoUnidade.getUnidade().getNome() + ")"
                            );
                        }
                ),
                PendenciaInativacaoDto.of(
                        TipoPendenciaInativacao.CASCATA,
                        "professor_unidade_modalidade",
                        "Professores vinculados a esta modalidade, que serão desvinculados.",
                        professorUnidadeModalidadeRepository.listarAtivosPorUnidadeModalidade(idsOferta, idEstabelecimento),
                        vinculo -> {
                            ProfessorUnidade professorUnidade = vinculo.getProfessorUnidade();
                            return new ItemPendenciaInativacaoDto(
                                    String.valueOf(vinculo.getId()),
                                    professorUnidade.getProfessor().getPessoa().getNome()
                                            + " (" + professorUnidade.getUnidade().getNome() + ")"
                            );
                        }
                )
        );
    }

    /**
     * Inativa as ofertas e seus vínculos de professor. Não valida pendências: quem chama
     * deve ter executado {@link #verificarPendenciasInativacao} na mesma transação.
     *
     * @return quantidade de registros dependentes inativados (ofertas + vínculos de professor)
     */
    @Transactional
    public int inativarEmCadeia(Collection<UnidadeModalidade> ofertas, Long idEstabelecimento) {
        if (ofertas.isEmpty()) {
            return 0;
        }

        List<Long> idsOferta = ofertas.stream().map(UnidadeModalidade::getId).toList();
        List<ProfessorUnidadeModalidade> vinculosProfessor = professorUnidadeModalidadeRepository
                .listarAtivosPorUnidadeModalidade(idsOferta, idEstabelecimento);

        vinculosProfessor.forEach(vinculo -> vinculo.setAtivo(Boolean.FALSE));
        ofertas.forEach(oferta -> oferta.setAtivo(Boolean.FALSE));

        return ofertas.size() + vinculosProfessor.size();
    }

    @Transactional(readOnly = true)
    public List<UnidadeModalidadeListagemDto> listar(Long idUnidade) {
        return unidadeModalidadeRepository.listarPorUnidade(idUnidade)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private UnidadeModalidadeListagemDto toDto(UnidadeModalidade unidadeModalidade) {
        Modalidade modalidade = unidadeModalidade.getModalidade();

        return new UnidadeModalidadeListagemDto(
                String.valueOf(unidadeModalidade.getId()),
                String.valueOf(unidadeModalidade.getUnidade().getId()),
                String.valueOf(modalidade.getId()),
                modalidade.getNome(),
                modalidade.getDescricao(),
                modalidade.getAtivo(),
                unidadeModalidade.getDescricao(),
                unidadeModalidade.getCapacidadePadrao(),
                unidadeModalidade.getAtivo()
        );
    }
}
