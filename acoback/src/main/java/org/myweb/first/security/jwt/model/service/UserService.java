package org.myweb.first.security.jwt.model.service;

import lombok.RequiredArgsConstructor;
import org.myweb.first.member.model.dto.Member;
import org.myweb.first.member.model.service.MemberService;
import org.myweb.first.security.jwt.filter.output.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *  LoginFilter 에서 두개의 서비스(@Service 로 등록된 클래스) 를 등록한 경우,
 *  authenticationManager 가 둘 중 사용할 서비스를 선택 못하는 문제가 발생함
 *  StackOverFlow : null  에러가 발생한 경우에 해결용으로 만드는 서비스 클래스임
 *  조건 : security 가 제공하는 UserDetailsService 인터페이스 상속받은 후손 만들기함
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //데이터베이스에서 사용자 정보를 조회해 옴
        Member member = memberService.selectMember(username);
        if (member == null) {
            throw new UsernameNotFoundException("조회된 회원 정보 없음 : " + username);
        }

        // UserDetails 를 상속받은 CustomUserDetails 객체로 반환 처리
        return new CustomUserDetails(member.toEntity());
    }
}
