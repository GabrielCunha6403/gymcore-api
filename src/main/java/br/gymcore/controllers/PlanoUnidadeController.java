package br.gymcore.controllers;

import br.gymcore.dtos.PlanoUnidadeListagemDto;
import br.gymcore.forms.PlanoUnidadeForm;
import br.gymcore.services.PlanoUnidadeService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plano-unidade")
@RequiredArgsConstructor
public class PlanoUnidadeController {

    private final PlanoUnidadeService planoUnidadeService;

    @GetMapping
    public ResponseEntity<List<PlanoUnidadeListagemDto>> listar(
            @RequestParam Long idUnidade
    ) {
        return ResponseEntity.ok(planoUnidadeService.listar(idUnidade));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> vincular(@Valid @RequestBody PlanoUnidadeForm form) {
        Long planoUnidadeId = planoUnidadeService.vincular(form);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Plano vinculado com sucesso",
                        "planoUnidadeId", String.valueOf(planoUnidadeId)
                ));
    }
}
