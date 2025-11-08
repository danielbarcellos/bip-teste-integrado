package com.example.beneficios.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beneficios.dto.BeneficioResponseDTO;
import com.example.beneficios.dto.TransferenciaDTO;
import com.example.beneficios.entity.Beneficio;
import com.example.beneficios.service.BeneficioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/beneficios")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Benefícios", description = "API para gerenciamento de benefícios")
public class BeneficioController {

    @Autowired
    private BeneficioService beneficioService;

    @GetMapping
    @Operation(summary = "Listar todos os benefícios")
    public ResponseEntity<List<BeneficioResponseDTO>> listarTodos() {
        List<Beneficio> beneficios = beneficioService.findAll();
        List<BeneficioResponseDTO> response = beneficios.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar benefício por ID")
    public ResponseEntity<BeneficioResponseDTO> buscarPorId(@PathVariable Long id) {
        Optional<Beneficio> beneficio = beneficioService.findById(id);
        return beneficio.map(b -> ResponseEntity.ok(toDTO(b)))
                       .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar novo benefício")
    public ResponseEntity<BeneficioResponseDTO> criar(@Valid @RequestBody Beneficio beneficio) {
        Beneficio salvo = beneficioService.save(beneficio);
        return ResponseEntity.ok(toDTO(salvo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar benefício")
    public ResponseEntity<BeneficioResponseDTO> atualizar(
            @PathVariable Long id, 
            @Valid @RequestBody Beneficio beneficio) {
        
        if (!beneficioService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        beneficio.setId(id);
        Beneficio atualizado = beneficioService.save(beneficio);
        return ResponseEntity.ok(toDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar benefício")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!beneficioService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        beneficioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transferir")
    @Operation(summary = "Transferir valor entre benefícios")
    public ResponseEntity<Map<String, String>> transferir(@Valid @RequestBody TransferenciaDTO transferencia) {
        try {
            beneficioService.transferir(transferencia);
            
            // RETORNE JSON EM VEZ DE STRING
            Map<String, String> response = new HashMap<>();
            response.put("message", "Transferência realizada com sucesso");
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "error");
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/ativos")
    @Operation(summary = "Listar benefícios ativos")
    public ResponseEntity<List<BeneficioResponseDTO>> listarAtivos() {
        List<Beneficio> beneficios = beneficioService.findByAtivo(true);
        List<BeneficioResponseDTO> response = beneficios.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/total-ativos")
    @Operation(summary = "Obter valor total dos benefícios ativos")
    public ResponseEntity<BigDecimal> getValorTotalAtivos() {
        BigDecimal total = beneficioService.getValorTotalAtivos();
        return ResponseEntity.ok(total);
    }

    private BeneficioResponseDTO toDTO(Beneficio beneficio) {
        return new BeneficioResponseDTO(
            beneficio.getId(),
            beneficio.getNome(),
            beneficio.getDescricao(),
            beneficio.getValor(),
            beneficio.getAtivo(),
            beneficio.getVersion()
        );
    }
}