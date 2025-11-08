package com.example.beneficios.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.beneficios.dto.TransferenciaDTO;
import com.example.beneficios.entity.Beneficio;
import com.example.beneficios.repository.BeneficioRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@ExtendWith(MockitoExtension.class)
class BeneficioServiceTest {

    @Mock
    private BeneficioRepository beneficioRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BeneficioService beneficioService;

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
    void testListarTodosBeneficios() {
        List<Beneficio> beneficios = Arrays.asList(beneficio1, beneficio2);
        when(beneficioRepository.findAll()).thenReturn(beneficios);

        List<Beneficio> resultado = beneficioService.findAll();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(beneficioRepository, times(1)).findAll();
    }

    @Test
    void testBuscarBeneficioPorId_Existente() {
        when(beneficioRepository.findById(1L)).thenReturn(Optional.of(beneficio1));

        Optional<Beneficio> resultado = beneficioService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Vale Alimentação", resultado.get().getNome());
        verify(beneficioRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarBeneficioPorId_NaoExistente() {
        when(beneficioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Beneficio> resultado = beneficioService.findById(99L);

        assertFalse(resultado.isPresent());
        verify(beneficioRepository, times(1)).findById(99L);
    }

    @Test
    void testCriarBeneficio() {
        Beneficio novoBeneficio = new Beneficio("Novo Benefício", "Descrição", new BigDecimal("300.00"));
        when(beneficioRepository.save(any(Beneficio.class))).thenReturn(novoBeneficio);

        Beneficio resultado = beneficioService.save(novoBeneficio);

        assertNotNull(resultado);
        assertEquals("Novo Benefício", resultado.getNome());
        verify(beneficioRepository, times(1)).save(novoBeneficio);
    }

    @Test
    void testDeletarBeneficio() {
        doNothing().when(beneficioRepository).deleteById(1L);

        beneficioService.deleteById(1L);

        verify(beneficioRepository, times(1)).deleteById(1L);
    }

    @Test
    void testTransferenciaComSucesso() {
        TransferenciaDTO transferencia = new TransferenciaDTO();
        transferencia.setFromId(1L);
        transferencia.setToId(2L);
        transferencia.setAmount(new BigDecimal("200.00"));

        when(entityManager.find(eq(Beneficio.class), eq(1L), eq(LockModeType.OPTIMISTIC)))
            .thenReturn(beneficio1);
        when(entityManager.find(eq(Beneficio.class), eq(2L), eq(LockModeType.OPTIMISTIC)))
            .thenReturn(beneficio2);
        when(beneficioRepository.save(any(Beneficio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        beneficioService.transferir(transferencia);

        assertEquals(new BigDecimal("800.00"), beneficio1.getValor());
        assertEquals(new BigDecimal("700.00"), beneficio2.getValor());
        verify(beneficioRepository, times(2)).save(any(Beneficio.class));
    }

    @Test
    void testTransferencia_SaldoInsuficiente() {
        TransferenciaDTO transferencia = new TransferenciaDTO();
        transferencia.setFromId(1L);
        transferencia.setToId(2L);
        transferencia.setAmount(new BigDecimal("2000.00")); // Valor maior que o saldo

        when(entityManager.find(eq(Beneficio.class), eq(1L), eq(LockModeType.OPTIMISTIC)))
            .thenReturn(beneficio1);
        when(entityManager.find(eq(Beneficio.class), eq(2L), eq(LockModeType.OPTIMISTIC)))
            .thenReturn(beneficio2);

        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> beneficioService.transferir(transferencia));
        
        assertTrue(exception.getMessage().contains("Saldo insuficiente"));
    }

    @Test
    void testTransferencia_BeneficioOrigemNaoEncontrado() {
        TransferenciaDTO transferencia = new TransferenciaDTO();
        transferencia.setFromId(99L);
        transferencia.setToId(2L);
        transferencia.setAmount(new BigDecimal("100.00"));

        when(entityManager.find(eq(Beneficio.class), eq(99L), eq(LockModeType.OPTIMISTIC)))
            .thenReturn(null);
        when(entityManager.find(eq(Beneficio.class), eq(2L), eq(LockModeType.OPTIMISTIC)))
            .thenReturn(beneficio2);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> beneficioService.transferir(transferencia));
        
        assertTrue(exception.getMessage().contains("Benefício origem não encontrado"));
    }

    @Test
    void testTransferencia_BeneficioInativo() {
        beneficio1.setAtivo(false);
        TransferenciaDTO transferencia = new TransferenciaDTO();
        transferencia.setFromId(1L);
        transferencia.setToId(2L);
        transferencia.setAmount(new BigDecimal("100.00"));

        when(entityManager.find(eq(Beneficio.class), eq(1L), eq(LockModeType.OPTIMISTIC)))
            .thenReturn(beneficio1);
        when(entityManager.find(eq(Beneficio.class), eq(2L), eq(LockModeType.OPTIMISTIC)))
            .thenReturn(beneficio2);

        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> beneficioService.transferir(transferencia));
        
        assertTrue(exception.getMessage().contains("não está ativo"));
    }

    @Test
    void testListarBeneficiosAtivos() {
        List<Beneficio> beneficiosAtivos = Arrays.asList(beneficio1);
        when(beneficioRepository.findByAtivo(true)).thenReturn(beneficiosAtivos);

        List<Beneficio> resultado = beneficioService.findByAtivo(true);

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getAtivo());
        verify(beneficioRepository, times(1)).findByAtivo(true);
    }

    @Test
    void testGetValorTotalAtivos() {
        when(beneficioRepository.sumValorByAtivo()).thenReturn(new BigDecimal("1500.00"));

        BigDecimal resultado = beneficioService.getValorTotalAtivos();

        assertEquals(new BigDecimal("1500.00"), resultado);
        verify(beneficioRepository, times(1)).sumValorByAtivo();
    }

    @Test
    void testGetValorTotalAtivos_QuandoNulo() {
        when(beneficioRepository.sumValorByAtivo()).thenReturn(null);

        BigDecimal resultado = beneficioService.getValorTotalAtivos();

        assertEquals(BigDecimal.ZERO, resultado);
    }
}