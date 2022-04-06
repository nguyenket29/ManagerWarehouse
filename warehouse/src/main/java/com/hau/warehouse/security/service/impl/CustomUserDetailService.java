package com.hau.warehouse.security.service.impl;

import com.hau.warehouse.entity.UserEntity;
import com.hau.warehouse.repository.IUserRepository;
import com.hau.warehouse.security.service.UserCustom;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private final IUserRepository userRepository;

    public CustomUserDetailService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));
        return UserCustom.build(user);
    }
}
