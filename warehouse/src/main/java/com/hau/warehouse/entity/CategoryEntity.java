package com.hau.warehouse.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "category")
public class CategoryEntity extends BaseEntity{
    @Column(name= "name")
    private String name;

    public Set<ProductEntity> getProductEntities() {
        return productEntities;
    }

    public void setProductEntities(Set<ProductEntity> productEntities) {
        this.productEntities = productEntities;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoryEntity")
    private Set<ProductEntity> productEntities;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
