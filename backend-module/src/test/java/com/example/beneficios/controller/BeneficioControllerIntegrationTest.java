package com.example.beneficios.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.example.beneficios.dto.TransferenciaDTO;
import com.example.beneficios.entity.Beneficio;
import com.example.beneficios.service.BeneficioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BeneficioController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {BeneficioController.class})
class BeneficioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BeneficioService beneficioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Beneficio beneficio1;
    private Beneficio beneficio2;

    @BeforeEach
    void setUp() {
        beneficio1 = new Beneficio("Vale Alimentação", "Benefício alimentação", new BigDecimal("1000.00"));
        beneficio1.setId(1L);
        beneficio1.setAtivo(true);

        beneficio2 = new Beneficio("Vale Transporte", "Benefício transporte", new BigDecimal("500.00"));
        beneficio2.setId(2L);
        beneficio2.setAtivo(true);
    }

    @Test
    void testListarTodosBeneficios() throws Exception {
        when(beneficioService.findAll()).thenReturn(Arrays.asList(beneficio1, beneficio2));

        mockMvc.perform(get("/beneficios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome", is("Vale Alimentação")))
                .andExpect(jsonPath("$[1].nome", is("Vale Transporte")));

        verify(beneficioService, times(1)).findAll();
    }

    @Test
    void testBuscarBeneficioPorId_Existente() throws Exception {
        when(beneficioService.findById(1L)).thenReturn(Optional.of(beneficio1));

        mockMvc.perform(get("/beneficios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Vale Alimentação")))
                .andExpect(jsonPath("$.valor", is(1000.00)));

        verify(beneficioService, times(1)).findById(1L);
    }

    @Test
    void testBuscarBeneficioPorId_NaoExistente() throws Exception {
        when(beneficioService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/beneficios/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(beneficioService, times(1)).findById(99L);
    }

    @Test
    void testCriarBeneficio() throws Exception {
        Beneficio novoBeneficio = new Beneficio("Novo Benefício", "Descrição", new BigDecimal("300.00"));
        novoBeneficio.setId(3L);
        
        when(beneficioService.save(any(Beneficio.class))).thenReturn(novoBeneficio);

        mockMvc.perform(post("/beneficios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoBeneficio)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nome", is("Novo Benefício")));

        verify(beneficioService, times(1)).save(any(Beneficio.class));
    }

    @Test
    void testAtualizarBeneficio() throws Exception {
        Beneficio beneficioAtualizado = new Beneficio("Vale Atualizado", "Descrição atualizada", new BigDecimal("1200.00"));
        beneficioAtualizado.setId(1L);
        
        when(beneficioService.findById(1L)).thenReturn(Optional.of(beneficio1));
        when(beneficioService.save(any(Beneficio.class))).thenReturn(beneficioAtualizado);

        mockMvc.perform(put("/beneficios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beneficioAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Vale Atualizado")))
                .andExpect(jsonPath("$.valor", is(1200.00)));

        verify(beneficioService, times(1)).save(any(Beneficio.class));
    }

    @Test
    void testDeletarBeneficio() throws Exception {
        when(beneficioService.findById(1L)).thenReturn(Optional.of(beneficio1));
        doNothing().when(beneficioService).deleteById(1L);

        mockMvc.perform(delete("/beneficios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(beneficioService, times(1)).deleteById(1L);
    }

    @Test
    void testTransferirComSucesso() throws Exception {
        TransferenciaDTO transferencia = new TransferenciaDTO();
        transferencia.setFromId(1L);
        transferencia.setToId(2L);
        transferencia.setAmount(new BigDecimal("200.00"));

        doNothing().when(beneficioService).transferir(any(TransferenciaDTO.class));

        mockMvc.perform(post("/beneficios/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferencia)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Transferência realizada com sucesso")))
                .andExpect(jsonPath("$.status", is("success")));

        verify(beneficioService, times(1)).transferir(any(TransferenciaDTO.class));
    }

    @Test
    void testTransferir_ValidacaoFalha() throws Exception {
        TransferenciaDTO transferenciaInvalida = new TransferenciaDTO();
        transferenciaInvalida.setFromId(1L);
        transferenciaInvalida.setToId(2L);
        transferenciaInvalida.setAmount(new BigDecimal("-100.00")); // Valor negativo

        mockMvc.perform(post("/beneficios/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferenciaInvalida)))
                .andExpect(status().isBadRequest());

        verify(beneficioService, never()).transferir(any(TransferenciaDTO.class));
    }

    @Test
    void testTransferir_ErroNoServico() throws Exception {
        TransferenciaDTO transferencia = new TransferenciaDTO();
        transferencia.setFromId(1L);
        transferencia.setToId(2L);
        transferencia.setAmount(new BigDecimal("200.00"));

        doThrow(new IllegalStateException("Saldo insuficiente"))
            .when(beneficioService).transferir(any(TransferenciaDTO.class));

        mockMvc.perform(post("/beneficios/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferencia)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Saldo insuficiente")))
                .andExpect(jsonPath("$.status", is("error")));

        verify(beneficioService, times(1)).transferir(any(TransferenciaDTO.class));
    }

    @Test
    void testListarBeneficiosAtivos() throws Exception {
        when(beneficioService.findByAtivo(true)).thenReturn(Arrays.asList(beneficio1));

        mockMvc.perform(get("/beneficios/ativos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Vale Alimentação")));

        verify(beneficioService, times(1)).findByAtivo(true);
    }

    @Test
    void testGetValorTotalAtivos() throws Exception {
        when(beneficioService.getValorTotalAtivos()).thenReturn(new BigDecimal("1500.00"));

        mockMvc.perform(get("/beneficios/total-ativos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1500.00"));

        verify(beneficioService, times(1)).getValorTotalAtivos();
    }
}