package br.com.goals.goals.repository;

import br.com.goals.goals.model.TransactionMb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionMbRepository extends JpaRepository<TransactionMb, Long> {
    List<TransactionMb> findByUserId(Long userId);
}
