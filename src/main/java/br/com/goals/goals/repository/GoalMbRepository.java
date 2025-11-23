package br.com.goals.goals.repository;

import br.com.goals.goals.model.GoalMb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalMbRepository extends JpaRepository<GoalMb, Long> {
    List<GoalMb> findByUserId(Long userId);
}

