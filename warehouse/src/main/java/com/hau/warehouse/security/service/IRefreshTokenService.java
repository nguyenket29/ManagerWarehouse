package com.hau.warehouse.security.service;

import com.hau.warehouse.entity.TokenRefreshEntity;

import java.util.Optional;

public interface IRefreshTokenService {
    Optional<TokenRefreshEntity> finByToken(String token);
    TokenRefreshEntity createRefreshToken(Long userId);
    TokenRefreshEntity verifyExpiration(TokenRefreshEntity tokenRefresh);
    int deleteByUserId(Long userId);
}
