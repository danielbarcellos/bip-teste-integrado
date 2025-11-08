package com.example.beneficios.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TransferenciaDTO {
    
    @NotNull(message = "ID origem é obrigatório")
    private Long fromId;
    
    @NotNull(message = "ID destino é obrigatório")
    private Long toId;
    
    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal amount;

    public Long getFromId() { return fromId; }
    public void setFromId(Long fromId) { this.fromId = fromId; }
    
    public Long getToId() { return toId; }
    public void setToId(Long toId) { this.toId = toId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    @Override
    public String toString() {
        return "TransferenciaDTO{" +
                "fromId=" + fromId +
                ", toId=" + toId +
                ", amount=" + amount +
                '}';
    }
}