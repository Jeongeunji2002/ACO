package org.myweb.first.member.jpa.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.myweb.first.member.jpa.entity.MemberEntity;
import org.myweb.first.member.jpa.entity.QMemberEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    //QueryDSL 사용방법 첫번째 :
    //2. MemberRepositoryCustom 인터페이스를 상속받는 후손 클래스를 만든다.
    //  => 후손 클래스이름은 부모인터페이스이름 + Impl 을 붙여줌

    //QueryDSL 에 대한 config 클래스를 먼저 만들고 나서 필드 선언함
    private final JPAQueryFactory queryFactory;     //이것만 선언하면 됨
    //반드시 final 사용할 것

    private final EntityManager entityManager;  //JPQL 사용을 위해 의존성 주입
    private QMemberEntity member = QMemberEntity.memberEntity;
    //member 테이블을 의미하는 MemberEntity 를 member 로 표현한다는 선언임

    //검색 관련 메소드 추가 ---------------------------------------------------
    @Override
    public List<MemberEntity> findSearchUserId(String keyword, Pageable pageable) {
        return queryFactory
                .selectFrom(member)
                .where(
                        member.userId.like("%" + keyword + "%")
                        .and(member.adminYN.eq("N"))
                )
                .orderBy(member.enrollDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countSearchUserId(String keyword) {
        return queryFactory
                .selectFrom(member)     //select * from notice
                .where(
                        member.userId.like("%" + keyword + "%")
                        .and(member.adminYN.eq("N"))
                )//where noticetitle %keyword%
                .fetchCount();
    }

    public List<MemberEntity> findSearchGender(String keyword, Pageable pageable) {
        return queryFactory
                .selectFrom(member)
                .where(
                        member.gender.eq(keyword)
                        .and(member.adminYN.eq("N"))
                )
                .orderBy(member.enrollDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countSearchGender(String keyword) {
        return queryFactory
                .selectFrom(member)     //select * from member
                .where(
                        member.gender.eq(keyword)
                        .and(member.adminYN.eq("N"))
                )//where gender = keyword
                .fetchCount();
    }

    @Override
    public List<MemberEntity> findSearchDate(Date begin, Date end, Pageable pageable) {
        return queryFactory
                .selectFrom(member)
                .where(
                        member.enrollDate.between(begin, end)
                        .and(member.adminYN.eq("N"))
                )
                .orderBy(member.enrollDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countSearchDate(Date begin, Date end) {
        return queryFactory
                .selectFrom(member)     //select * from member
                .where(
                        member.enrollDate.between(begin, end)
                        .and(member.adminYN.eq("N"))
                )  //where enroll_date between :begin and :end
                .fetchCount();
    }

    @Override
    public List<MemberEntity> findSearchAge(int age, Pageable pageable) {
        return queryFactory
                .selectFrom(member)
                .where(
                        member.age.between(age, age + 9)
                        .and(member.adminYN.eq("N"))
                )
                .orderBy(member.enrollDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countSearchAge(int age) {
        return queryFactory
                .selectFrom(member)
                .where(
                        member.age.between(age, age + 9)
                        .and(member.adminYN.eq("N"))
                )
                .fetchCount();
    }

    @Override
    public List<MemberEntity> findSearchLoginOK(String keyword, Pageable pageable) {
        return queryFactory
                .selectFrom(member)
                .where(
                        member.loginOk.eq(keyword)
                        .and(member.adminYN.eq("N"))
                )
                .orderBy(member.enrollDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countSearchLoginOK(String keyword) {
        return queryFactory
                .selectFrom(member)     //select * from member
                .where(
                        member.loginOk.eq(keyword)
                        .and(member.adminYN.eq("N"))
                 )  //where loginOk = keyword
                .fetchCount();
    }
}
