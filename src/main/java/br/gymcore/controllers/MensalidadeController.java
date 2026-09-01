package br.gymcore.controllers;

import br.gymcore.dtos.MensalidadeListagemDto;
import br.gymcore.services.MensalidadeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mensalidade")
@RequiredArgsConstructor
public class MensalidadeController {

    private final MensalidadeService mensalidadeService;

    @GetMapping
    public ResponseEntity<List<MensalidadeListagemDto>> listar(
            @RequestParam Long idAluno
    ) {
        return ResponseEntity.ok(mensalidadeService.listarPorAluno(idAluno));
    }
}
