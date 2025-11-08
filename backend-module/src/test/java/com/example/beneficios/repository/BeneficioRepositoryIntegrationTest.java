package com.example.beneficios.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.example.beneficios.entity.Beneficio;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BeneficioRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BeneficioRepository beneficioRepository;

    @Test
    void testSalvarEBuscarBeneficio() {
        Beneficio beneficio = new Beneficio("Teste", "Descrição teste", new BigDecimal("1000.00"));
        beneficio.setAtivo(true);

        Beneficio salvo = entityManager.persistAndFlush(beneficio);
        Optional<Beneficio> encontrado = beneficioRepository.findById(salvo.getId());

        assertTrue(encontrado.isPresent());
        assertEquals("Teste", encontrado.get().getNome());
        assertEquals(new BigDecimal("1000.00"), encontrado.get().getValor());
        assertTrue(encontrado.get().getAtivo());
    }

    @Test
    void testBuscarPorAtivo() {
        Beneficio ativo = new Beneficio("Ativo", "Ativo", new BigDecimal("500.00"));
        ativo.setAtivo(true);
        
        Beneficio inativo = new Beneficio("Inativo", "Inativo", new BigDecimal("300.00"));
        inativo.setAtivo(false);

        entityManager.persist(ativo);
        entityManager.persist(inativo);
        entityManager.flush();

        List<Beneficio> ativos = beneficioRepository.findByAtivo(true);
        List<Beneficio> inativos = beneficioRepository.findByAtivo(false);

        assertEquals(1, ativos.size());
        assertEquals("Ativo", ativos.get(0).getNome());
        
        assertEquals(1, inativos.size());
        assertEquals("Inativo", inativos.get(0).getNome());
    }

    @Test
    void testCalcularSomaValoresAtivos() {
        Beneficio beneficio1 = new Beneficio("B1", "Desc1", new BigDecimal("1000.00"));
        beneficio1.setAtivo(true);
        
        Beneficio beneficio2 = new Beneficio("B2", "Desc2", new BigDecimal("500.00"));
        beneficio2.setAtivo(true);
        
        Beneficio beneficio3 = new Beneficio("B3", "Desc3", new BigDecimal("300.00"));
        beneficio3.setAtivo(false);

        entityManager.persist(beneficio1);
        entityManager.persist(beneficio2);
        entityManager.persist(beneficio3);
        entityManager.flush();

        BigDecimal soma = beneficioRepository.sumValorByAtivo();

        assertNotNull(soma);
        assertEquals(0, new BigDecimal("1500.00").compareTo(soma));
    }

    @Test
    void testBuscarPorIdEAtivo() {
        Beneficio beneficio = new Beneficio("Buscar Teste", "Descrição", new BigDecimal("400.00"));
        beneficio.setAtivo(true);
        Beneficio salvo = entityManager.persistAndFlush(beneficio);

        Optional<Beneficio> encontrado = beneficioRepository.findByIdAndAtivo(salvo.getId(), true);

        assertTrue(encontrado.isPresent());
        assertEquals("Buscar Teste", encontrado.get().getNome());
    }

    @Test
    void testBuscarPorIdEAtivo_NaoEncontrado() {
        Beneficio beneficio = new Beneficio("Inativo", "Descrição", new BigDecimal("400.00"));
        beneficio.setAtivo(false);
        Beneficio salvo = entityManager.persistAndFlush(beneficio);

        Optional<Beneficio> encontrado = beneficioRepository.findByIdAndAtivo(salvo.getId(), true);

        assertFalse(encontrado.isPresent());
    }

    @Test
    void testBuscarPorValorMaiorQue() {
        Beneficio beneficio1 = new Beneficio("Alto", "Valor alto", new BigDecimal("800.00"));
        Beneficio beneficio2 = new Beneficio("Baixo", "Valor baixo", new BigDecimal("300.00"));
        
        entityManager.persist(beneficio1);
        entityManager.persist(beneficio2);
        entityManager.flush();

        List<Beneficio> resultados = beneficioRepository.findByValorMaiorQue(new BigDecimal("500.00"));

        assertEquals(1, resultados.size());
        assertEquals("Alto", resultados.get(0).getNome());
    }
}