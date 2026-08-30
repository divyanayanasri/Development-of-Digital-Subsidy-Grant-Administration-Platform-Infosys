package com.training.common.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "regions")
public class Region {
    public Region() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(precision = 15, scale = 2)
    private BigDecimal budgetCap;

    @Column(precision = 15, scale = 2)
    private BigDecimal budgetUsed;

    public Region(Long id, String name, BigDecimal budgetCap, BigDecimal budgetUsed) {
        this.id = id;
        this.name = name;
        this.budgetCap = budgetCap;
        this.budgetUsed = budgetUsed;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getBudgetCap() { return budgetCap; }
    public void setBudgetCap(BigDecimal budgetCap) { this.budgetCap = budgetCap; }

    public BigDecimal getBudgetUsed() { return budgetUsed; }
    public void setBudgetUsed(BigDecimal budgetUsed) { this.budgetUsed = budgetUsed; }

    public static RegionBuilder builder() {
        return new RegionBuilder();
    }

    public static class RegionBuilder {
        private Long id;
        private String name;
        private BigDecimal budgetCap;
        private BigDecimal budgetUsed;

        public RegionBuilder id(Long id) { this.id = id; return this; }
        public RegionBuilder name(String name) { this.name = name; return this; }
        public RegionBuilder budgetCap(BigDecimal budgetCap) { this.budgetCap = budgetCap; return this; }
        public RegionBuilder budgetUsed(BigDecimal budgetUsed) { this.budgetUsed = budgetUsed; return this; }

        public Region build() {
            return new Region(id, name, budgetCap, budgetUsed);
        }
    }
}
