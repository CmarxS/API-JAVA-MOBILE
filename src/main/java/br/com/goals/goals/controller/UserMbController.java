package br.com.goals.goals.controller;

import br.com.goals.goals.model.UserMb;
import br.com.goals.goals.repository.UserMbRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserMbController {

    private final UserMbRepository repository;

    public UserMbController(UserMbRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<UserMb> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserMb> findById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserMb> findByEmail(@PathVariable String email) {
        return repository.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserMb create(@RequestBody UserMb user) {
        user.setId(null); // garante que será um insert
        return repository.save(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserMb> update(@PathVariable Long id, @RequestBody UserMb body) {
        return repository.findById(id)
                .map(existing -> {
                    body.setId(existing.getId());
                    return ResponseEntity.ok(repository.save(body));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<UserMb> updateName(@PathVariable Long id, @RequestBody String nome) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setNome(nome);
                    return ResponseEntity.ok(repository.save(existing));
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

