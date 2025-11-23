package br.com.goals.goals.controller;

import br.com.goals.goals.model.CategoryMb;
import br.com.goals.goals.repository.CategoryMbRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryMbController {

    private final CategoryMbRepository repository;

    public CategoryMbController(CategoryMbRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<CategoryMb> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryMb> findById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CategoryMb create(@RequestBody CategoryMb category) {
        category.setId(null);
        return repository.save(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryMb> update(@PathVariable Long id, @RequestBody CategoryMb body) {
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

