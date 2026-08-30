package com.training.module1_masterdata.controller;

import com.training.common.entity.Scheme;
import com.training.common.dto.SchemeCriteriaDTO;
import com.training.module1_masterdata.dto.SchemeDTO;
import com.training.module1_masterdata.service.SchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping({"/api/admin/schemes", "/api/schemes"})
@Validated
public class SchemeController {

    @Autowired
    private SchemeService schemeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Scheme> createScheme(@Valid @RequestBody SchemeDTO dto) {
        Scheme scheme = schemeService.createScheme(dto);
        return new ResponseEntity<>(scheme, HttpStatus.CREATED);
    }

    @Autowired
    private com.training.module1_masterdata.repository.SchemeRepository schemeRepository;

    @GetMapping
    public ResponseEntity<?> getAllSchemes(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            return ResponseEntity.ok(schemeRepository.findAll(org.springframework.data.domain.PageRequest.of(page, size)));
        }
        List<Scheme> schemes = schemeService.getAllSchemes();
        return ResponseEntity.ok(schemes);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Scheme> updateScheme(@PathVariable("id") Long id, @Valid @RequestBody SchemeDTO dto) {
        Scheme scheme = schemeService.updateScheme(id, dto);
        return ResponseEntity.ok(scheme);
    }

    @GetMapping("/{id}/criteria")
    public ResponseEntity<SchemeCriteriaDTO> getSchemeCriteria(@PathVariable("id") Long id) {
        SchemeCriteriaDTO criteria = schemeService.getSchemeCriteria(id);
        return ResponseEntity.ok(criteria);
    }
}
