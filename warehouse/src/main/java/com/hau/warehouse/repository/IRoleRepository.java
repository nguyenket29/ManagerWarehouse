package com.hau.warehouse.repository;

import com.hau.warehouse.constant.RoleEnum;
import com.hau.warehouse.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByCode(RoleEnum code);
}
