package com.example.beneficios.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "BENEFICIO")
@NamedQueries({
    @NamedQuery(name = "Beneficio.findAll", query = "SELECT b FROM Beneficio b"),
    @NamedQuery(name = "Beneficio.findByAtivo", query = "SELECT b FROM Beneficio b WHERE b.ativo = :ativo")
})
public class Beneficio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;
    
    @Column(name = "DESCRICAO", length = 255)
    private String descricao;
    
    @NotNull(message = "Valor é obrigatório")
    @PositiveOrZero(message = "Valor deve ser positivo ou zero")
    @Column(name = "VALOR", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;
    
    @Column(name = "ATIVO")
    private Boolean ativo;
    
    @Version
    @Column(name = "VERSION")
    private Long version;

    public Beneficio() {
        this.ativo = true;
        this.valor = BigDecimal.ZERO;
    }

    public Beneficio(String nome, String descricao, BigDecimal valor) {
        this();
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    @Override
    public String toString() {
        return "Beneficio{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", valor=" + valor +
                ", ativo=" + ativo +
                ", version=" + version +
                '}';
    }
}