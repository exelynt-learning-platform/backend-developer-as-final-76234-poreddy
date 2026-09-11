package com.example.booking.controller;

import com.example.booking.dto.ResourceDto;
import com.example.booking.entity.ResourceEntity;
import com.example.booking.repository.ResourceRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {
    private final com.example.booking.service.ResourceService service;

    public ResourceController(com.example.booking.service.ResourceService service) { this.service = service; }

    @GetMapping
    public List<ResourceDto> list() { return service.list(); }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceDto> get(@PathVariable Long id) { return service.get(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ResourceDto> create(@Valid @RequestBody ResourceDto dto) {
        ResourceDto created = service.create(dto);
        return ResponseEntity.created(URI.create("/api/resources/" + created.id())).body(created);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ResourceDto> update(@PathVariable Long id, @Valid @RequestBody ResourceDto dto) {
        return service.update(id, dto).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build(); }
}
