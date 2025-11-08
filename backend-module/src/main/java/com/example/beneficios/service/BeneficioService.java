package com.example.beneficios.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beneficios.dto.TransferenciaDTO;
import com.example.beneficios.entity.Beneficio;
import com.example.beneficios.repository.BeneficioRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class BeneficioService {

    private static final Logger logger = LoggerFactory.getLogger(BeneficioService.class);
    
    @Autowired
    private BeneficioRepository beneficioRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public List<Beneficio> findAll() {
        return beneficioRepository.findAll();
    }

    public Optional<Beneficio> findById(Long id) {
        return beneficioRepository.findById(id);
    }

    public Beneficio save(Beneficio beneficio) {
        return beneficioRepository.save(beneficio);
    }

    public void deleteById(Long id) {
        beneficioRepository.deleteById(id);
    }

    public List<Beneficio> findByAtivo(Boolean ativo) {
        return beneficioRepository.findByAtivo(ativo);
    }

    /**
     * CORREÇÃO: Transferência com locking otimista e validações
     */
    public void transferir(TransferenciaDTO transferencia) {
        logger.info("Iniciando transferência: {}", transferencia);
        
        Long fromId = transferencia.getFromId();
        Long toId = transferencia.getToId();
        BigDecimal amount = transferencia.getAmount();
        
        // Validações iniciais
        validarTransferencia(fromId, toId, amount);
        
        // Buscar benefícios com LOCKING OTIMISTA
        Beneficio from = entityManager.find(Beneficio.class, fromId, LockModeType.OPTIMISTIC);
        Beneficio to = entityManager.find(Beneficio.class, toId, LockModeType.OPTIMISTIC);
        
        validarBeneficios(from, to);
        
        // Validar saldo suficiente
        if (from.getValor().compareTo(amount) < 0) {
            throw new IllegalStateException(
                String.format("Saldo insuficiente no benefício %s. Saldo: R$ %.2f, Valor transferência: R$ %.2f", 
                from.getNome(), from.getValor(), amount)
            );
        }
        
        // Executar transferência
        from.setValor(from.getValor().subtract(amount));
        to.setValor(to.getValor().add(amount));
        
        // Salvar alterações
        beneficioRepository.save(from);
        beneficioRepository.save(to);
        
        logger.info("Transferência concluída: R$ {} de {} para {}", amount, from.getNome(), to.getNome());
    }

    private void validarTransferencia(Long fromId, Long toId, BigDecimal amount) {
        if (fromId == null || toId == null || amount == null) {
            throw new IllegalArgumentException("Parâmetros não podem ser nulos");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser positivo");
        }
        
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Não é possível transferir para o mesmo benefício");
        }
    }

    private void validarBeneficios(Beneficio from, Beneficio to) {
        if (from == null) {
            throw new IllegalArgumentException("Benefício origem não encontrado");
        }
        if (to == null) {
            throw new IllegalArgumentException("Benefício destino não encontrado");
        }
        if (!from.getAtivo()) {
            throw new IllegalStateException("Benefício origem não está ativo: " + from.getNome());
        }
        if (!to.getAtivo()) {
            throw new IllegalStateException("Benefício destino não está ativo: " + to.getNome());
        }
    }

    public BigDecimal getValorTotalAtivos() {
        BigDecimal total = beneficioRepository.sumValorByAtivo();
        return total != null ? total : BigDecimal.ZERO;
    }
}
