package br.com.goals.goals.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "TB_GOALS_MB")
public class GoalMb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_goal")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private UserMb user;

    private String titulo;

    private String tipo;

    @Column(name = "valor_alvo")
    private BigDecimal valorAlvo;

    @Column(name = "dias_alvo")
    private Integer diasAlvo;

    @Column(name = "dias_concluidos")
    private Integer diasConcluidos;

    @Column(name = "qtd_alvo_diaria")
    private Integer qtdAlvoDiaria;

    private String unidade;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    private String status;

    @Column(name = "created_at")
    private Instant createdAt;
}

