package br.gymcore.controllers;

import br.gymcore.dtos.UnidadeModalidadeListagemDto;
import br.gymcore.forms.UnidadeModalidadeForm;
import br.gymcore.services.UnidadeModalidadeService;
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
@RequestMapping("/unidade-modalidade")
@RequiredArgsConstructor
public class UnidadeModalidadeController {

    private final UnidadeModalidadeService unidadeModalidadeService;

    @GetMapping
    public ResponseEntity<List<UnidadeModalidadeListagemDto>> listar(
            @RequestParam Long idUnidade
    ) {
        return ResponseEntity.ok(unidadeModalidadeService.listar(idUnidade));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> vincular(@Valid @RequestBody UnidadeModalidadeForm form) {
        Long unidadeModalidadeId = unidadeModalidadeService.vincular(form);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Modalidade vinculada com sucesso",
                        "unidadeModalidadeId", String.valueOf(unidadeModalidadeId)
                ));
    }
}
