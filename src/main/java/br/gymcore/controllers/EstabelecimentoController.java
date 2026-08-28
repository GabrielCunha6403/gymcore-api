package br.gymcore.controllers;

import br.gymcore.dtos.EstabelecimentoListagemDto;
import br.gymcore.forms.EstabelecimentoForm;
import br.gymcore.services.EstabelecimentoService;
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
@RequestMapping("/estabelecimento")
@RequiredArgsConstructor
public class EstabelecimentoController {

    private final EstabelecimentoService estabelecimentoService;

    @GetMapping
    public ResponseEntity<List<EstabelecimentoListagemDto>> listar(
            @RequestParam(required = false) String busca
    ) {
        return ResponseEntity.ok(estabelecimentoService.listar(busca));
    }

    @GetMapping("/getEstabelecimentoById")
    public ResponseEntity<EstabelecimentoListagemDto> getEstabelecimentoById(@RequestParam(required = false) Long idEstabelecimento) {
        return ResponseEntity.ok(estabelecimentoService.getEstabelecimentoById(idEstabelecimento));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> cadastrar(@Valid @RequestBody EstabelecimentoForm form) {
        Long estabelecimentoId = estabelecimentoService.cadastrar(form);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Estabelecimento cadastrado com sucesso",
                        "estabelecimentoId", String.valueOf(estabelecimentoId)
                ));
    }
}
