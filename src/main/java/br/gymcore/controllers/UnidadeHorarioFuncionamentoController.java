package br.gymcore.controllers;

import br.gymcore.dtos.UnidadeHorarioFuncionamentoListagemDto;
import br.gymcore.forms.UnidadeHorarioFuncionamentoForm;
import br.gymcore.services.UnidadeHorarioFuncionamentoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/unidade-horario-funcionamento")
@RequiredArgsConstructor
public class UnidadeHorarioFuncionamentoController {

    private final UnidadeHorarioFuncionamentoService unidadeHorarioFuncionamentoService;

    @GetMapping
    public ResponseEntity<List<UnidadeHorarioFuncionamentoListagemDto>> listar(
            @RequestParam Long idUnidade
    ) {
        return ResponseEntity.ok(unidadeHorarioFuncionamentoService.listar(idUnidade));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> salvar(@Valid @RequestBody UnidadeHorarioFuncionamentoForm form) {
        unidadeHorarioFuncionamentoService.salvar(form);

        return ResponseEntity.ok(Map.of("message", "Horário de funcionamento salvo com sucesso"));
    }
}
