package org.myweb.first.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.member.model.dto.Member;
import org.myweb.first.security.jwt.filter.input.InputMember;
import org.myweb.first.member.model.service.MemberService;
import org.myweb.first.security.jwt.filter.output.CustomUserDetails;
import org.myweb.first.security.jwt.jpa.entity.RefreshToken;
import org.myweb.first.security.jwt.model.service.RefreshService;
import org.myweb.first.security.jwt.model.service.UserService;
import org.myweb.first.security.jwt.util.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;
    private final RefreshService refreshService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private static final long ACCESS_TOKEN_EXPIRATION = 1800000L;
    private static final long REFRESH_TOKEN_EXPIRATION = 86400000L;

    //생성자를 통한 의존성 주입
    public LoginFilter(UserService userService, RefreshService refreshService,
                       AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.userService = userService;
        this.refreshService = refreshService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;

        //로그인 url 요청에 대한 앤드포인트 설정
        setFilterProcessesUrl("/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        log.info("LoginFilter running attemptAuthentication()");
        try {
            InputMember loginData = new ObjectMapper().readValue(request.getInputStream(), InputMember.class);
            log.info("login data: {}", loginData);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginData.getUserId(), loginData.getUserPwd());
            log.info("authToken: {}", authToken);
            
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse login request", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {
        // 인증절차가 성공되면 자동으로 구동됨
        log.info("LoginFilter successfulAuthentication()");

        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        String username = userDetails.getUsername();  //로그인한 userId 임
        log.info("successfulAuthentication: {}", username);

        //로그인 성공이므로, 토큰 만들기함
        String accessToken = jwtUtil.generateToken(username, "access", 1800000L);
        String refreshToken = jwtUtil.generateToken(username, "refresh", 86400000L);

        //만들어진 리프레시토큰 정보 db 에 저장함
        RefreshToken tokenEntity = RefreshToken.builder()
                .id(UUID.randomUUID())
                .userId(username)
                .tokenValue(refreshToken)
                .expiresIn(86400000L)
                .status("activated")
                .build();
        refreshService.save(tokenEntity);

        response.addHeader("Authorization", "Bearer " + accessToken);

        Map<String, Object> body = new HashMap<>();
        body.put("refreshToken", refreshToken);

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), body);
    }

    //로그인 실패시 실행되는 메소드임.
    //실패할 경우 HTTP 상태 코드 401 을 반환함
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException exception) throws IOException {
        // 인증 조회가 실패하면 자동 구동됨
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Authentication failed");
        responseBody.put("error", exception.getMessage());

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), responseBody);
    }

}
