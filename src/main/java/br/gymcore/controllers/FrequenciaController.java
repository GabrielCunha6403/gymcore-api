package br.gymcore.controllers;

import br.gymcore.dtos.FrequenciaListagemDto;
import br.gymcore.services.FrequenciaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frequencia")
@RequiredArgsConstructor
public class FrequenciaController {

    private final FrequenciaService frequenciaService;

    @GetMapping
    public ResponseEntity<List<FrequenciaListagemDto>> listar(
            @RequestParam Long idAluno
    ) {
        return ResponseEntity.ok(frequenciaService.listarPorAluno(idAluno));
    }
}
