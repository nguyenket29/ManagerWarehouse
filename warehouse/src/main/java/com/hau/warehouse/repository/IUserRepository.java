package com.hau.warehouse.repository;

import com.hau.warehouse.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findById(Long id);
    Optional<UserEntity> findByToken(String token);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
