package com.training.module4_analytics.controller;

import com.training.common.entity.Region;
import com.training.module1_masterdata.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/regions")
public class RegionController {

    @Autowired
    private RegionRepository regionRepository;

    @GetMapping
    public ResponseEntity<List<Region>> getAllRegions() {
        return ResponseEntity.ok(regionRepository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Region> createRegion(@RequestBody Region region) {
        if (region.getBudgetUsed() == null) {
            region.setBudgetUsed(java.math.BigDecimal.ZERO);
        }
        Region saved = regionRepository.save(region);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Region> updateRegion(@PathVariable("id") Long id, @RequestBody Region regionDetails) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Region not found with ID: " + id));
        region.setName(regionDetails.getName());
        region.setBudgetCap(regionDetails.getBudgetCap());
        if (regionDetails.getBudgetUsed() != null) {
            region.setBudgetUsed(regionDetails.getBudgetUsed());
        }
        Region updated = regionRepository.save(region);
        return ResponseEntity.ok(updated);
    }
}
