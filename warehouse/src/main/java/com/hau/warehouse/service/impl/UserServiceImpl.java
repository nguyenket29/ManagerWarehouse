package com.hau.warehouse.service.impl;

import com.hau.warehouse.constant.RoleEnum;
import com.hau.warehouse.entity.*;
import com.hau.warehouse.exception.CustomNotFoundException;
import com.hau.warehouse.exception.CustomExistException;
import com.hau.warehouse.exception.CustomTokenRefreshException;
import com.hau.warehouse.payload.request.LoginRequest;
import com.hau.warehouse.payload.request.TokenRefreshRequest;
import com.hau.warehouse.payload.request.UserRequest;
import com.hau.warehouse.payload.response.LoginResponse;
import com.hau.warehouse.payload.response.MessageResponse;
import com.hau.warehouse.payload.response.TokenRefreshResponse;
import com.hau.warehouse.repository.IDepartmentRepository;
import com.hau.warehouse.repository.IRoleRepository;
import com.hau.warehouse.repository.IUserInfoRepository;
import com.hau.warehouse.repository.IUserRepository;
import com.hau.warehouse.security.UserCustom;
import com.hau.warehouse.security.jwt.JwtUtils;
import com.hau.warehouse.service.IControllAdvice;
import com.hau.warehouse.service.IRefreshTokenService;
import com.hau.warehouse.service.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {
    private final AuthenticationManager authenticationManager;

    private final IUserRepository userRepository;

    private final IRoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final IDepartmentRepository departmentRepository;

    private final IUserInfoRepository userInfoRepository;

    private final JwtUtils jwtUtils;

    private final IRefreshTokenService refreshTokenService;

    public UserServiceImpl(AuthenticationManager authenticationManager, IUserRepository userRepository,
                           IRoleRepository roleRepository, PasswordEncoder encoder,
                           IDepartmentRepository departmentRepository,
                           IUserInfoRepository userInfoRepository, JwtUtils jwtUtils
            , IRefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.departmentRepository = departmentRepository;
        this.userInfoRepository = userInfoRepository;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public ResponseEntity<?> login(LoginRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserCustom userCustom = (UserCustom) authentication.getPrincipal();

        List<String> roles = userCustom.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        TokenRefreshEntity tokenRefresh = refreshTokenService.createRefreshToken(userCustom.getId());

        return ResponseEntity.ok(new LoginResponse(jwt, tokenRefresh.getToken() , userCustom.getId(),
                userCustom.getUsername(), userCustom.getEmail(), roles));
    }

    @Override
    public ResponseEntity<?> register(UserRequest userRequest){
        if(userRepository.existsByUsername(userRequest.getUsername())){
            return ResponseEntity.badRequest().body(new CustomExistException("Username is exist !"));
        }

        if (userRepository.existsByEmail(userRequest.getEmail())){
            return ResponseEntity.badRequest().body(new CustomExistException("Email is exist !"));
        }

        DepartmentEntity department = departmentRepository.findById(userRequest.getDepartmentId()).isPresent()
                ? departmentRepository.findById(userRequest.getDepartmentId()).get() : null;

        UserInfoEntity userInfo = userInfoRepository.findById(userRequest.getUserInfoId()).isPresent()
                ? userInfoRepository.findById(userRequest.getUserInfoId()).get() : null;

        UserEntity user = new UserEntity(userRequest.getUsername(),encoder.encode(userRequest.getPassword()),
                userRequest.getEmail(), userRequest.getPosition(), department, userInfo);

        Set<String> roles = userRequest.getRole();
        Set<RoleEntity> role = new HashSet<>();

        if (roles == null || roles.isEmpty()){
            RoleEntity roleEntity = roleRepository.findByCode(RoleEnum.ROLE_EMPLOYEE)
                    .orElseThrow(() -> new CustomNotFoundException("Role Employee not found !"));
            role.add(roleEntity);
        }else {
            roles.forEach(item -> {
                switch (item){
                    case "ROLE_MANAGER":
                        RoleEntity roleManager = roleRepository.findByCode(RoleEnum.ROLE_MANAGER)
                                .orElseThrow(() -> new CustomNotFoundException("Role Manager not found !"));
                        role.add(roleManager);
                        break;
                    case "ROLE_EMPLOYEE":
                        RoleEntity roleEmployee = roleRepository.findByCode(RoleEnum.ROLE_EMPLOYEE)
                                .orElseThrow(() -> new CustomNotFoundException("Role Employee not found !"));
                        role.add(roleEmployee);
                    default:
                        throw new CustomNotFoundException("Role not found !");
                }
            });
        }

        user.setRoles(role);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User Register Successfully !"));
    }

    @Override
    public ResponseEntity<?> TokenRefresh(TokenRefreshRequest tokenRequest) {
        String requestTokenRefresh = tokenRequest.getRefreshToken();
        return refreshTokenService.finByToken(requestTokenRefresh)
                .map(refreshTokenService::verifyExpiration)
                .map(TokenRefreshEntity::getUser)
                .map(u -> {
                    String token = jwtUtils.generateTokenFromUsername(u.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestTokenRefresh));
                }).orElseThrow(() ->
                        new CustomTokenRefreshException(requestTokenRefresh, "Refresh token is not in database"));
    }
}
