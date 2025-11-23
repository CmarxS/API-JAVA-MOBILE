package br.com.goals.goals.controller;

import br.com.goals.goals.model.TransactionMb;
import br.com.goals.goals.repository.TransactionMbRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionMbController {

    private final TransactionMbRepository repository;

    public TransactionMbController(TransactionMbRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<TransactionMb> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionMb> findById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<TransactionMb> findByUserId(@PathVariable Long userId) {
        return repository.findByUserId(userId);
    }

    @PostMapping
    public TransactionMb create(@RequestBody TransactionMb tx) {
        tx.setId(null);
        return repository.save(tx);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionMb> update(@PathVariable Long id, @RequestBody TransactionMb body) {
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


