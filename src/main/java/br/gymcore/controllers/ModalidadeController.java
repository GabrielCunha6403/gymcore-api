package br.gymcore.controllers;

import br.gymcore.dtos.ModalidadeGeralListagemDto;
import br.gymcore.dtos.ModalidadeListagemDto;
import br.gymcore.dtos.PageDto;
import br.gymcore.forms.ModalidadeForm;
import br.gymcore.services.ModalidadeService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/modalidade")
@RequiredArgsConstructor
public class ModalidadeController {

    private final ModalidadeService modalidadeService;

    @GetMapping
    public ResponseEntity<List<ModalidadeListagemDto>> listar(
            @RequestParam(required = false) Long idEstabelecimento,
            @RequestParam(required = false) String busca
    ) {
        return ResponseEntity.ok(modalidadeService.listar(idEstabelecimento, busca));
    }

    @GetMapping("/geral")
    public ResponseEntity<PageDto<ModalidadeGeralListagemDto>> listarGeral(
            @RequestParam(required = false) String busca,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable
    ) {
        return ResponseEntity.ok(modalidadeService.listarGeral(busca, pageable));
    }

    @GetMapping("/getModalidadeById")
    public ResponseEntity<ModalidadeListagemDto> getModalidadeById(
            @RequestParam Long idModalidade
    ) {
        return ResponseEntity.ok(modalidadeService.getModalidadeById(idModalidade));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> cadastrar(@Valid @RequestBody ModalidadeForm form) {
        Long modalidadeId = modalidadeService.cadastrar(form);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Modalidade cadastrada com sucesso",
                        "modalidadeId", String.valueOf(modalidadeId)
                ));
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> atualizar(
            @RequestParam Long idModalidade,
            @Valid @RequestBody ModalidadeForm form
    ) {
        modalidadeService.atualizar(idModalidade, form);

        return ResponseEntity.ok(Map.of("message", "Modalidade atualizada com sucesso"));
    }
}
