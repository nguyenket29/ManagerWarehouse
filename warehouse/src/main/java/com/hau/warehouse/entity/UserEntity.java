package com.hau.warehouse.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "position")
    private String position;

    public UserEntity() {
    }

    public UserEntity(String username, String password, String email,
                      String position, DepartmentEntity departmentEntity, UserInfoEntity userInfoEntity) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.position = position;
        this.departmentEntity = departmentEntity;
        this.userInfoEntity = userInfoEntity;
    }

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "roles_users", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_info_id", referencedColumnName = "id", unique = true)
    private UserInfoEntity userInfoEntity;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private DepartmentEntity departmentEntity;

    @OneToOne(mappedBy = "user" ,
            cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TokenRefreshEntity tokenRefresh;

    public TokenRefreshEntity getTokenRefresh() {
        return tokenRefresh;
    }

    public void setTokenRefresh(TokenRefreshEntity tokenRefresh) {
        this.tokenRefresh = tokenRefresh;
    }

    public UserEntity(String username, String email, String password) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public DepartmentEntity getDepartmentEntity() {
        return departmentEntity;
    }

    public void setDepartmentEntity(DepartmentEntity departmentEntity) {
        this.departmentEntity = departmentEntity;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserInfoEntity getUserInfoEntity() {
        return userInfoEntity;
    }

    public void setUserInfoEntity(UserInfoEntity userInfoEntity) {
        this.userInfoEntity = userInfoEntity;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }
}
