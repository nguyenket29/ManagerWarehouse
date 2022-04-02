package com.hau.warehouse.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "department")
public class DepartmentEntity extends BaseEntity {
    @Column(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "departmentEntity", cascade = CascadeType.ALL)
    private Set<UserEntity> userEntity;

    public Set<UserEntity> getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(Set<UserEntity> userEntity) {
        this.userEntity = userEntity;
    }
}
