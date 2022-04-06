package com.hau.warehouse.entity;

import javax.persistence.*;

@Entity
@Table(name = "product")
public class ProductEntity extends BaseEntity{
    @Column(name = "name")
    private String name;

    @Column(name = "unit")
    private String unit;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity categoryEntity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public CategoryEntity getCategoryEntity() {
        return categoryEntity;
    }

    public void setCategoryEntity(CategoryEntity categoryEntity) {
        this.categoryEntity = categoryEntity;
    }
}
