package com.hau.warehouse.security.jwt;

import com.hau.warehouse.security.service.impl.CustomUserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class TokenFilter extends BasicAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(TokenFilter.class);

    private final JwtUtils jwtUtils;

    private final CustomUserDetailService userDetailService;

    public TokenFilter(AuthenticationManager authenticationManager, JwtUtils jwtUtils
            , CustomUserDetailService userDetailService) {
        super(authenticationManager);
        this.jwtUtils = jwtUtils;
        this.userDetailService = userDetailService;
    }


    private String parseJwt(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        if(StringUtils.hasText(header) && header.startsWith("Bearer ")){
            return header.substring(7, header.length());
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try{
            String jwt = parseJwt(request);
            if(jwt != null && jwtUtils.validateJwtToken(jwt)){
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                UserDetails userDetails = userDetailService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }catch (Exception e){
            logger.error("Can't set authentication: " + e);
        }
        chain.doFilter(request, response);
    }
}
