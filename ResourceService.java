package com.example.booking.service;

import com.example.booking.dto.ResourceDto;
import com.example.booking.entity.ResourceEntity;
import com.example.booking.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResourceService {
    private final ResourceRepository repo;

    public ResourceService(ResourceRepository repo) { this.repo = repo; }

    public List<ResourceDto> list() {
        return repo.findAll().stream().map(r -> new ResourceDto(r.getId(), r.getName(), r.getDescription())).collect(Collectors.toList());
    }

    public Optional<ResourceDto> get(Long id) {
        return repo.findById(id).map(r -> new ResourceDto(r.getId(), r.getName(), r.getDescription()));
    }

    public ResourceDto create(ResourceDto dto) {
        ResourceEntity r = new ResourceEntity();
        r.setName(dto.name()); r.setDescription(dto.description());
        r = repo.save(r);
        return new ResourceDto(r.getId(), r.getName(), r.getDescription());
    }

    public Optional<ResourceDto> update(Long id, ResourceDto dto) {
        return repo.findById(id).map(r -> {
            r.setName(dto.name()); r.setDescription(dto.description());
            repo.save(r);
            return new ResourceDto(r.getId(), r.getName(), r.getDescription());
        });
    }

    public boolean delete(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }
}
