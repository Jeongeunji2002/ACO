package org.myweb.first.security.jwt.jpa.repository;

import org.myweb.first.security.jwt.jpa.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshRepository extends JpaRepository<RefreshToken, UUID> {
    //tokenValue 를 전달받아, db에 조회해서 토큰 객체를 리턴
    Optional<RefreshToken> findByTokenValue(String tokenValue);

    //토큰값이 db에 존재하는지 확인하는 용도
    Boolean existsByTokenValue(String refresh);

    //로그아웃시 토큰 삭제용
    void deleteByTokenValue(String refresh);

    //엑세스 토큰에 등록된 회원아이디(userid)로 리프레쉬 토큰 조회용
    @Query("SELECT r FROM RefreshToken r WHERE r.userId = :userId")
    List<RefreshToken> findByUserId(@Param("userId") String userId);
}
