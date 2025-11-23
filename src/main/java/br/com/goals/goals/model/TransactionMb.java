package br.com.goals.goals.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "TB_TRANSACTIONS_MB")
public class TransactionMb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private UserMb user;

    @ManyToOne
    @JoinColumn(name = "id_category")
    private CategoryMb category;

    @ManyToOne
    @JoinColumn(name = "id_goal")
    private GoalMb goal;

    private String tipo;

    private BigDecimal valor;

    private String descricao;

    private String merchant;

    @Column(name = "data_transacao")
    private LocalDate dataTransacao;

    @Column(name = "created_at")
    private Instant createdAt;
}