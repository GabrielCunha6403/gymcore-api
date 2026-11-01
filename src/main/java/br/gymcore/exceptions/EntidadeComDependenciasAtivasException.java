package br.gymcore.exceptions;

import br.gymcore.dtos.VerificacaoInativacaoDto;
import lombok.Getter;

@Getter
public class EntidadeComDependenciasAtivasException extends RuntimeException {

    private final transient VerificacaoInativacaoDto verificacao;

    public EntidadeComDependenciasAtivasException(String message, VerificacaoInativacaoDto verificacao) {
        super(message);
        this.verificacao = verificacao;
    }
}
