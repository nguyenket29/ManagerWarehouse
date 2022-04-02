package com.hau.warehouse.service.impl;

import com.hau.warehouse.entity.TokenRefreshEntity;
import com.hau.warehouse.exception.CustomTokenRefreshException;
import com.hau.warehouse.repository.IRefreshTokenRepository;
import com.hau.warehouse.repository.IUserRepository;
import com.hau.warehouse.service.IRefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements IRefreshTokenService {
    @Value("${jwt.jwtRefreshExpiration}")
    private Long refreshTokenExpiraton;

    private final IRefreshTokenRepository tokenRepository;
    private final IUserRepository userRepository;

    public RefreshTokenServiceImpl(IRefreshTokenRepository tokenRepository, IUserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<TokenRefreshEntity> finByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public TokenRefreshEntity createRefreshToken(Long userId) {
        TokenRefreshEntity tokenRefresh = new TokenRefreshEntity();
        tokenRefresh.setUser(userRepository.findById(userId).isPresent()
                ? userRepository.findById(userId).get() : null);
        tokenRefresh.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiraton));
        tokenRefresh.setToken(UUID.randomUUID().toString());
        tokenRefresh = tokenRepository.save(tokenRefresh);
        return tokenRefresh;
    }

    @Override
    public TokenRefreshEntity verifyExpiration(TokenRefreshEntity tokenRefresh) {
        if (tokenRefresh.getExpiryDate().compareTo(Instant.now()) < 0) {
            tokenRepository.delete(tokenRefresh);
            throw new CustomTokenRefreshException(tokenRefresh.getToken(), "Refresh token was expired !");
        }
        return tokenRefresh;
    }

    @Override
    @Transactional
    public int deleteByUserId(Long userId) {
        return tokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
