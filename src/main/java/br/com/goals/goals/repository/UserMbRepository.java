package br.com.goals.goals.repository;

import br.com.goals.goals.model.UserMb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMbRepository extends JpaRepository<UserMb, Long> {
    Optional<UserMb> findByEmail(String email);
}

