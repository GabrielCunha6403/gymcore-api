package br.gymcore.controllers;

import br.gymcore.dtos.PageDto;
import br.gymcore.dtos.ProfessorListagemDto;
import br.gymcore.forms.ProfessorForm;
import br.gymcore.services.ProfessorService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/professor")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    @GetMapping
    public ResponseEntity<PageDto<ProfessorListagemDto>> listar(
            @RequestParam(required = false) String busca,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(professorService.listar(busca, pageable));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> cadastrar(@Valid @RequestBody ProfessorForm form) {
        Long professorId = professorService.cadastrar(form);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Professor cadastrado com sucesso",
                        "professorId", String.valueOf(professorId)
                ));
    }
}
