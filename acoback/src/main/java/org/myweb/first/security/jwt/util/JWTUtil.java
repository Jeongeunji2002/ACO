package org.myweb.first.security.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.member.model.dto.Member;
import org.myweb.first.member.model.service.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.Base64;

@Slf4j
@Component  //스프링 부트 컨테이너에 의해 관리되는 컴포넌트로 선언함
//@PropertySource("classpath:application.properties")  // application.properties 가 연결되지 않을 때 이용함
public class JWTUtil {
    //JWT 생성과 검증에 사용될 비밀키와 만료시간을 필드로 선언함
    private SecretKeySpec secretKey;
    private final MemberService memberService;

    @Value("${jwt.secret}")
    private String secretKeyString;

    //생성자를 통한 의존성 주입
    public JWTUtil(MemberService memberService) {
        this.memberService = memberService;
    }

    //application.porperties 에 정의한 jwt 비밀키 읽어와서 지정하기 위해 @PostConstruct 설정해야 함
    @PostConstruct
    public void init(){
        //secretKey 초기화
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        this.secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    //JWT 토큰 생성 : 인증(athentication)시 받은 사용자 아이디를 전달받아서 확인하고 토큰생성함
    //userId : 로그인 요청시 넘어온 회원 아이디 받음, category : 토큰의 종류(access, refresh),
    //expiredMs : 만료기한에 대한 밀리초
    public String generateToken(String userId, String category, Long expiredMs) {
        log.info("generate token : {}", secretKey);

        //MemberSerive 사용해서 db 에서 로그인한 사용자 정보를 조회해 옴
        Member member = memberService.selectMember(userId);

        //사용자 정보가 없는 경우, UsernameNotFoundException (스프링 제공됨)을 발생시킴
        if (member == null) {
            throw new UsernameNotFoundException("userId : " + userId + "not found.");
        }

        //사용자의 관리자 여부 확인
        String adminYN = member.getAdminYN();

        //JWT 토큰 생성 : 사용자 아이디(subject)에 넣고, 관리자여부는 클레임으로 추가함 (임의대로 지정함)
        return Jwts.builder()
                .setSubject(userId)  // 사용자 ID 설정 (로그인시 이메일 사용시에는 이메일 등록)
                .claim("category", category)  // 카테고리 정보 추가 ("access", "refresh")
                .claim("name", member.getUserName())  // 사용자 이름 또는 닉네임 추가
                .claim("role", (adminYN.equals("Y")? "ADMIN": "USER"))  // ROLE 정보 추가
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))  // 토큰 만료 시간 설정
                .signWith(secretKey, SignatureAlgorithm.HS256)  // 비밀키와 알고리즘으로 서명
                .compact();  // JWT 생성 : JWT 를 압축 문자열로 만듦
    }

    // 공통 Claims 추출 메서드 (중복 코드 줄이기 위함)
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰에서 subject (userId) 추출
    public String getUserIdFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    // 토큰에서 만료시간(millisecond) 추출
    public boolean isTokenExpired(String token) {
        return getClaimsFromToken(token).getExpiration().before(new java.util.Date());
    }

    // 토큰에서 category 추출
    public String getCategoryFromToken(String token) {
        return getClaimsFromToken(token).get("category", String.class);
    }

    // 토큰에서 role 추출
    public String getRoleFromToken(String token) {
        return getClaimsFromToken(token).get("role", String.class);
    }

}












