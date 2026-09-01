package br.gymcore.controllers;

import br.gymcore.dtos.PlanoListagemDto;
import br.gymcore.forms.PlanoForm;
import br.gymcore.services.PlanoService;
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
@RequestMapping("/plano")
@RequiredArgsConstructor
public class PlanoController {

    private final PlanoService planoService;

    @GetMapping
    public ResponseEntity<List<PlanoListagemDto>> listar(
            @RequestParam(required = false) Long idEstabelecimento,
            @RequestParam(required = false) String busca
    ) {
        return ResponseEntity.ok(planoService.listar(idEstabelecimento, busca));
    }

    @GetMapping("/getPlanoById")
    public ResponseEntity<PlanoListagemDto> getPlanoById(
            @RequestParam Long idPlano
    ) {
        return ResponseEntity.ok(planoService.getPlanoById(idPlano));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> cadastrar(@Valid @RequestBody PlanoForm form) {
        Long planoId = planoService.cadastrar(form);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Plano cadastrado com sucesso",
                        "planoId", String.valueOf(planoId)
                ));
    }
}
