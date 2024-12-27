package org.myweb.first.security.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.member.model.service.MemberService;
import org.myweb.first.security.handler.CustomLogoutHandler;
import org.myweb.first.security.jwt.filter.JWTFilter;
import org.myweb.first.security.jwt.filter.LoginFilter;
import org.myweb.first.security.jwt.model.service.RefreshService;
import org.myweb.first.security.jwt.model.service.UserService;
import org.myweb.first.security.jwt.util.JWTUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;


@Slf4j
//@RequiredArgsConstructor  //선언된 필드에 대한 자동 의존성 주입 처리하는 어노테이션
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RefreshService refreshService;
    private final UserService userService;
    private final JWTUtil jwtUtil;

    //직접 생성자를 작성해서 초기화 선언함
    public SecurityConfig(RefreshService refreshService, UserService userService, JWTUtil jwtUtil) {
        this.refreshService = refreshService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //인증 (Authentication) 관리자를 스프링 부트 컨테이너에 Bean 으로 등록해야 함
    //인증 과정에서 중요한 클래스임
    @Bean
    public AuthenticationManager authenticationManager() {
        // authenticationManager 두 개의 서비스로 인해 StackOverflow 가 발생한 경우
        // UserDetailsService 를 상속받은 서비스를 기본으로 사용하도록
        // 지정하는 코드로 변경함
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);  // UserDetailsService 구현체 (UserService) 사용을 지정함
        provider.setPasswordEncoder(passwordEncoder()); //패스워드 암호화처리할 클래스 지정
        return new ProviderManager(provider);
    }

    //HTTP 관련 보안 설정을 정의함
    //SecurityFilterChain 을 Bean 으로 등록하고, http 서비스 요청에 대한 보안 설정을 구성함
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // CORS 활성화
                .formLogin(form -> form.disable())  //시큐리티가 제공하는 로그인 폼 사용 못하게 함
                .httpBasic(basic -> basic.disable())  //form 태그로 submit 해서 오는 요청은 사용 못하게 함
                //인증과 인가를 설정하는 부분
                .authorizeHttpRequests(auth -> auth
                        //현재 프로젝트 안에 뷰페이지를 작업할 때 설정하는 방식임 (리액트 작업시 제외)
                        //.requestMatchers("/public/**", "/auth/**", "/css/**", "/js/**").permitAll() // 공개 경로 설정 및 인증 경로 허용
                       //jwt 사용시 추가되는 설정임 ----------------------------
                        //공지사항 관리자용 서비스 요청 설정
                        .requestMatchers(HttpMethod.POST, "/notice").hasRole("ADMIN")   // POST 요청은 ADMIN 롤 필요
                        .requestMatchers(HttpMethod.PUT, "/notice/{noticeNo}").hasRole("ADMIN")    // PUT 요청은 ADMIN 롤 필요
                        .requestMatchers(HttpMethod.DELETE, "/notice/{noticeNo}").hasRole("ADMIN") // DELETE 요청은 ADMIN 롤 필요
                        //게시글 서비스
                        .requestMatchers(HttpMethod.POST, "/board").hasAnyRole("USER", "ADMIN") // POST 요청은 USER 롤 필요
                        .requestMatchers(HttpMethod.PUT, "/board").hasAnyRole("USER", "ADMIN") // PUT 요청은 USER 롤 필요
                        .requestMatchers(HttpMethod.DELETE, "/board/{boardNum}").hasAnyRole("USER", "ADMIN") // DELETE 요청은 USER 롤 필요
                        // 댓글 서비스
                        .requestMatchers(HttpMethod.POST, "/reply").hasAnyRole("USER", "ADMIN") // POST 요청은 USER 롤 필요
                        .requestMatchers(HttpMethod.PUT, "/reply/{replyNum}").hasAnyRole("USER", "ADMIN") // PUT 요청은 USER 롤 필요
                        .requestMatchers(HttpMethod.DELETE, "/reply/{replyNum}").hasAnyRole("USER", "ADMIN") // DELETE 요청은 USER 롤 필요
                        //회원 서비스
                        .requestMatchers(HttpMethod.GET, "/member/myinfo/{userId}").hasAnyRole("USER", "ADMIN") // GET 요청은 USER 롤 필요
                        .requestMatchers(HttpMethod.PUT, "/member/{userId}").hasRole("USER") // PUT 요청은 USER 롤 필요
                        .requestMatchers(HttpMethod.DELETE, "/member/{userId}").hasRole("USER") // DELETE 요청은 USER 롤 필요
                        //회원관리 서비스
                        .requestMatchers(HttpMethod.PUT, "/member/loginok/{userId}/{loginOk}").hasRole("ADMIN") // PUT 요청은 ADMIN 롤 필요
                        .requestMatchers(HttpMethod.GET, "/member/search").hasRole("ADMIN") // GET 요청은 ADMIN 롤 필요
                        .requestMatchers(HttpMethod.GET, "/member").hasRole("ADMIN") // GET 요청은 ADMIN 롤 필요
                        // permitAll() : url 접근을 허용한다는 의미임. 첫번째로 작동됨
                        // permitAll() 에 등록되지 않은 url 은 서버에 접속 못하게 됨
                        .requestMatchers("/login", "/reissue",  "/notice/**", "/board/**", "/member/**", "/**").permitAll()

                        .requestMatchers("/logout").authenticated()   //로그아웃 요청은 로그인한 사용자만 가능
                        //위의 인가 설정을 제외한 나머지 요청들은 인증을 거치도록 설정함
                        .anyRequest().authenticated()
                )
                // JWT 필터와 Login 필터 추가
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new LoginFilter(userService, refreshService, authenticationManager(), jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(new CustomLogoutHandler(jwtUtil, userService, refreshService))
                        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK)))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // 디버그용 필터
    static class DebugFilter implements Filter {
        private final String filterName;

        public DebugFilter(String filterName) {
            this.filterName = filterName;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            log.info("[DEBUG] 작동 필터 : " + filterName);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                log.info("[DEBUG] 인가 : " + authentication.getName()
                        + ", 인증 : " + authentication.getAuthorities());
            } else {
                log.info("[DEBUG] Authentication is null");
            }
            chain.doFilter(request, response);
            log.info("[DEBUG] 종료 Filter : " + filterName);
        }
    }
}

