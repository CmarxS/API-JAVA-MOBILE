package br.com.goals.goals.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "TB_CATEGORIES_MB")
public class CategoryMb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_category")
    private Long id;

    private String nome;

    private String tipo;

    @Column(name = "limite_mensal")
    private BigDecimal limiteMensal;

    @Column(name = "created_at")
    private Instant createdAt;
}

