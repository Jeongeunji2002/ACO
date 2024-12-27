package org.myweb.first.security.jwt.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.member.jpa.entity.MemberEntity;
import org.myweb.first.security.jwt.filter.output.CustomUserDetails;
import org.myweb.first.security.jwt.util.JWTUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("JWT filter running...");
        try {
            String authorization = request.getHeader("Authorization");
            String requestURI = request.getRequestURI();

            // 로그인 및 토큰 재발급 요청은 필터 통과
            if (requestURI.equals("/login") || requestURI.equals("/reissue")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Authorization 헤더가 없거나 잘못된 형식이면 필터 통과
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authorization.split(" ")[1];

            // 토큰 만료 여부 확인
            if (jwtUtil.isTokenExpired(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Access token expired");
                return;
            }

            // 토큰 카테고리 확인
            String category = jwtUtil.getCategoryFromToken(token);
            if (!"access".equals(category)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid access token");
                return;
            }

            // 사용자 정보 추출
            String username = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            // 사용자 인증 객체 생성
            MemberEntity member = new MemberEntity();
            member.setUserId(username);
            member.setAdminYN(role.equals("ADMIN") ? "Y" : "N");
            member.setUserPwd("tempPassword");

            log.info("Authenticated member: {}", member);

            // ROLE_ Prefix 추가: Spring Security에서 권한을 인식하려면 ROLE_ prefix가 필요함.
            // SimpleGrantedAuthority: GrantedAuthority를 정확하게 설정합니다.
            // CustomUserDetails 및 Authentication 객체 생성
            CustomUserDetails customUserDetails = new CustomUserDetails(member);
            Authentication authToken = new UsernamePasswordAuthenticationToken(
                    customUserDetails,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
            );

            // SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Access token expired");
        } catch (Exception e) {
            log.error("Error in JWTFilter: ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred during JWT processing.");
        }
    }
}
