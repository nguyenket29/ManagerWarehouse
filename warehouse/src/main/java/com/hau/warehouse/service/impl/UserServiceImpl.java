package com.hau.warehouse.service.impl;

import com.hau.warehouse.constant.RoleEnum;
import com.hau.warehouse.entity.*;
import com.hau.warehouse.exception.*;
import com.hau.warehouse.payload.request.LoginRequest;
import com.hau.warehouse.payload.request.TokenRefreshRequest;
import com.hau.warehouse.payload.request.UserRequest;
import com.hau.warehouse.payload.response.APIResponse;
import com.hau.warehouse.payload.response.LoginResponse;
import com.hau.warehouse.payload.response.MessageResponse;
import com.hau.warehouse.payload.response.TokenRefreshResponse;
import com.hau.warehouse.repository.IDepartmentRepository;
import com.hau.warehouse.repository.IRoleRepository;
import com.hau.warehouse.repository.IUserInfoRepository;
import com.hau.warehouse.repository.IUserRepository;
import com.hau.warehouse.security.jwt.JwtUtils;
import com.hau.warehouse.security.service.IRefreshTokenService;
import com.hau.warehouse.security.service.UserCustom;
import com.hau.warehouse.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderMail;

    public UserServiceImpl(AuthenticationManager authenticationManager, IUserRepository userRepository,
                           IRoleRepository roleRepository, PasswordEncoder encoder,
                           IDepartmentRepository departmentRepository,
                           IUserInfoRepository userInfoRepository, JwtUtils jwtUtils
            , IRefreshTokenService refreshTokenService, JavaMailSender javaMailSender) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.departmentRepository = departmentRepository;
        this.userInfoRepository = userInfoRepository;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.javaMailSender = javaMailSender;
    }

    @Override
    @Transactional
    public ResponseEntity<?> login(LoginRequest request) {
        UserEntity userEntity = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomNotFoundException("Username not found !"));

        if (userEntity.isActive()){
            String jwt = jwtUtils.generateJwtToken(auth(request.getUsername(), request.getPassword()));

            UserCustom userCustom = (UserCustom)
                    auth(request.getUsername(), request.getPassword()).getPrincipal();

            List<String> roles = userCustom.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

            TokenRefreshEntity tokenRefresh = refreshTokenService.createRefreshToken(userCustom.getId());

            userEntity.setToken(jwt);
            userRepository.save(userEntity);

            return ResponseEntity.ok(new LoginResponse(jwt, tokenRefresh.getToken() , userCustom.getId(),
                    userCustom.getUsername(), userCustom.getEmail(), roles));
        }

        return new ResponseEntity<>("User hasn't been permission !", HttpStatus.UNAUTHORIZED);
    }

    private Authentication auth(String username, String password){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
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
                    .orElseThrow(() -> {
                        throw new CustomNotFoundException("Role Employee not found !");
                    });
            role.add(roleEntity);
        }else {
            roles.forEach(item -> {
                switch (item){
                    case "ROLE_MANAGER":
                        RoleEntity roleManager = roleRepository.findByCode(RoleEnum.ROLE_MANAGER)
                                .orElseThrow(() -> {
                                    throw new CustomNotFoundException("Role Manager not found !");
                                });
                        role.add(roleManager);
                        break;
                    default:
                        RoleEntity roleEmployee = roleRepository.findByCode(RoleEnum.ROLE_EMPLOYEE)
                                .orElseThrow(() -> {
                                    throw new CustomNotFoundException("Role Employee not found !");
                                });
                        role.add(roleEmployee);
                }
            });
        }

        user.setRoles(role);
        user.setActive(true);
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

    private ResponseEntity<?> validateAndResetPassword(UserEntity user, String newPassword) {
        if (StringUtils.isEmpty(newPassword)) {
            return new ResponseEntity<>("Password cannot be empty", HttpStatus.BAD_REQUEST);
        }
        user.setPassword((encoder.encode(newPassword)));
        user.setToken(StringUtils.EMPTY);
        userRepository.save(user);

        return new ResponseEntity<>("Password reset successfully", HttpStatus.OK);
    }

    private ResponseEntity<?> sendPasswordResetLink(UserEntity user, String url) {
        String token = UUID.randomUUID().toString();
        user.setToken(token);

        String msg = getMailBody(url, token);
        try {
            MimeMessage mimeMessage = getMimeMessage(user, msg);
            javaMailSender.send(mimeMessage);
            userRepository.save(user);
            return new ResponseEntity<>("Password reset link has been to your email. ", HttpStatus.OK);
        } catch (MailException | MessagingException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failure", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> resetPassword(UserEntity user) {
        return userRepository.findByToken(user.getToken())
                .map(u -> this.validateAndResetPassword(u, user.getPassword()))
                .orElseGet(() -> new ResponseEntity("Invalid request", HttpStatus.BAD_REQUEST));
    }

    @Override
    public ResponseEntity<?> forgotPassword(String email, String url) {
        return userRepository.findByEmail(email)
                .map(e -> this.sendPasswordResetLink(e, url))
                .orElseGet(() -> new ResponseEntity("Failure", HttpStatus.NOT_FOUND));
    }


    private MimeMessage getMimeMessage(UserEntity user, String msg) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setFrom(senderMail);
        mimeMessageHelper.setTo(user.getEmail());
        mimeMessageHelper.setSubject("Reset password !");
        mimeMessageHelper.setText(msg, true);
        return mimeMessage;
    }

    private String getMailBody(String url, String token){
        String start = StringUtils.join("<a href=", url , "/reset-password?token=", token, ">");
        String end = "</a>";
        return StringUtils.join("Click ", start, " here ", end, " to reset password !");
    }
}
