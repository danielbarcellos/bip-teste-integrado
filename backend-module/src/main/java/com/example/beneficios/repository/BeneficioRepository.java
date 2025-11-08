package com.example.beneficios.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.beneficios.entity.Beneficio;

@Repository
public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {
    
    List<Beneficio> findByAtivo(Boolean ativo);
    
    Optional<Beneficio> findByIdAndAtivo(Long id, Boolean ativo);
    
    @Query("SELECT b FROM Beneficio b WHERE b.valor > :valorMinimo")
    List<Beneficio> findByValorMaiorQue(@Param("valorMinimo") BigDecimal valorMinimo);
    
    @Query("SELECT SUM(b.valor) FROM Beneficio b WHERE b.ativo = true")
    BigDecimal sumValorByAtivo();
}
