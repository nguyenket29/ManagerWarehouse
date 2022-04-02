package com.hau.warehouse.entity;

import com.hau.warehouse.constant.RoleEnum;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "roles")
public class RoleEntity extends BaseEntity {
    @Column(unique = true, nullable = false , name = "name")
    @Enumerated(EnumType.STRING)
    private RoleEnum name;

    @Column(unique = true, nullable = false,name = "code")
    @Enumerated(EnumType.STRING)
    private RoleEnum code;

    public RoleEnum getName() {
        return name;
    }

    public void setName(RoleEnum name) {
        this.name = name;
    }

    public RoleEnum getCode() {
        return code;
    }

    public void setCode(RoleEnum code) {
        this.code = code;
    }

    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users;

    public Set<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }
}
