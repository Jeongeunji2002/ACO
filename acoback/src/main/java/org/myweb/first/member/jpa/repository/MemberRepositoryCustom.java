package org.myweb.first.member.jpa.repository;

import org.myweb.first.member.jpa.entity.MemberEntity;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.util.List;

//QueryDSL 용 Custom 인터페이스를 작성함
public interface MemberRepositoryCustom {
    // QueryDSL 사용방법 첫번째 : 1. 추가하는 메소드에 대해 인터페이스 만들고, 추상메소드로 선언함

    //추가한 메소드에 대한 추상메소드로 선언
     List<MemberEntity> findSearchUserId(String keyword, Pageable pageable);
     long countSearchUserId(String keyword);
     List<MemberEntity> findSearchGender(String keyword, Pageable pageable);
     long countSearchGender(String keyword);
     List<MemberEntity> findSearchDate(Date begin, Date end, Pageable pageable);
     long countSearchDate(Date begin, Date end);
     List<MemberEntity> findSearchAge(int age, Pageable pageable);
     long countSearchAge(int age);
     List<MemberEntity> findSearchLoginOK(String keyword, Pageable pageable);
     long countSearchLoginOK(String keyword);
}
