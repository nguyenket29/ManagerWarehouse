package com.hau.warehouse.repository;

import com.hau.warehouse.entity.TokenRefreshEntity;
import com.hau.warehouse.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRefreshTokenRepository extends JpaRepository<TokenRefreshEntity, Long> {
    Optional<TokenRefreshEntity> findById(Long id);
    Optional<TokenRefreshEntity> findByToken(String token);
    int deleteByUser(UserEntity user);
}
