package br.gymcore.controllers;

import br.gymcore.dtos.UnidadeListagemDto;
import br.gymcore.forms.UnidadeForm;
import br.gymcore.services.UnidadeService;
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
@RequestMapping("/unidade")
@RequiredArgsConstructor
public class UnidadeController {

    private final UnidadeService unidadeService;

    @GetMapping
    public ResponseEntity<List<UnidadeListagemDto>> listar(
            @RequestParam(required = false) Long idEstabelecimento,
            @RequestParam(required = false) String busca
    ) {
        return ResponseEntity.ok(unidadeService.listar(idEstabelecimento, busca));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> cadastrar(@Valid @RequestBody UnidadeForm form) {
        Long unidadeId = unidadeService.cadastrar(form);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Unidade cadastrada com sucesso",
                        "unidadeId", String.valueOf(unidadeId)
                ));
    }
}
