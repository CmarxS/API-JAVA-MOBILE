package br.com.goals.goals.controller;

import br.com.goals.goals.model.GoalMb;
import br.com.goals.goals.repository.GoalMbRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalMbController {

    private final GoalMbRepository repository;

    public GoalMbController(GoalMbRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<GoalMb> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalMb> findById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<GoalMb> findByUserId(@PathVariable Long userId) {
        return repository.findByUserId(userId);
    }

    @PostMapping
    public GoalMb create(@RequestBody GoalMb goal) {
        goal.setId(null);
        return repository.save(goal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalMb> update(@PathVariable Long id, @RequestBody GoalMb body) {
        return repository.findById(id)
                .map(existing -> {
                    body.setId(existing.getId());
                    return ResponseEntity.ok(repository.save(body));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

