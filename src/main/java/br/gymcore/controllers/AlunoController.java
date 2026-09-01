package br.gymcore.controllers;

import br.gymcore.dtos.AlunoDetalheDto;
import br.gymcore.dtos.AlunoListagemDto;
import br.gymcore.dtos.PageDto;
import br.gymcore.forms.AlunoForm;
import br.gymcore.services.AlunoService;
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
@RequestMapping("/aluno")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;

    @GetMapping
    public ResponseEntity<PageDto<AlunoListagemDto>> listar(
            @RequestParam(required = false) String busca,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(alunoService.listar(busca, pageable));
    }

    @GetMapping("/getAlunoById")
    public ResponseEntity<AlunoDetalheDto> getAlunoById(
            @RequestParam Long idAluno
    ) {
        return ResponseEntity.ok(alunoService.getAlunoById(idAluno));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> cadastrar(@Valid @RequestBody AlunoForm form) {
        Long alunoId = alunoService.cadastrar(form);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Aluno cadastrado com sucesso",
                        "alunoId", String.valueOf(alunoId)
                ));
    }
}
