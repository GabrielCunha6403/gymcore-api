package br.gymcore.controllers;

import br.gymcore.dtos.AlunoTurmaListagemDto;
import br.gymcore.forms.AlunoTurmaForm;
import br.gymcore.services.AlunoTurmaService;
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
@RequestMapping("/aluno-turma")
@RequiredArgsConstructor
public class AlunoTurmaController {

    private final AlunoTurmaService alunoTurmaService;

    @GetMapping
    public ResponseEntity<List<AlunoTurmaListagemDto>> listar(
            @RequestParam Long idMatricula
    ) {
        return ResponseEntity.ok(alunoTurmaService.listarPorMatricula(idMatricula));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> matricular(@Valid @RequestBody AlunoTurmaForm form) {
        Long alunoTurmaId = alunoTurmaService.matricular(form);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Aluno inscrito na turma com sucesso",
                        "alunoTurmaId", String.valueOf(alunoTurmaId)
                ));
    }
}
