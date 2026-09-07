package br.gymcore.controllers;

import br.gymcore.dtos.TurmaAlunoListagemDto;
import br.gymcore.dtos.TurmaListagemDto;
import br.gymcore.forms.TurmaForm;
import br.gymcore.services.TurmaService;
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
@RequestMapping("/turma")
@RequiredArgsConstructor
public class TurmaController {

    private final TurmaService turmaService;

    @GetMapping
    public ResponseEntity<List<TurmaListagemDto>> listar(
            @RequestParam Long idUnidade
    ) {
        return ResponseEntity.ok(turmaService.listarPorUnidade(idUnidade));
    }

    @GetMapping("/getTurmaById")
    public ResponseEntity<TurmaListagemDto> getTurmaById(
            @RequestParam Long idTurma
    ) {
        return ResponseEntity.ok(turmaService.getTurmaById(idTurma));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<TurmaListagemDto>> listarDisponiveis(
            @RequestParam Long idMatricula
    ) {
        return ResponseEntity.ok(turmaService.listarDisponiveisParaMatricula(idMatricula));
    }

    @GetMapping("/alunos")
    public ResponseEntity<List<TurmaAlunoListagemDto>> listarAlunos(
            @RequestParam Long idTurma
    ) {
        return ResponseEntity.ok(turmaService.listarAlunos(idTurma));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> cadastrar(@Valid @RequestBody TurmaForm form) {
        Long turmaId = turmaService.cadastrar(form);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Turma cadastrada com sucesso",
                        "turmaId", String.valueOf(turmaId)
                ));
    }
}
