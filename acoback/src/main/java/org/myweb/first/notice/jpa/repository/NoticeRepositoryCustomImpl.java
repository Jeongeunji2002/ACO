package org.myweb.first.notice.jpa.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.myweb.first.notice.jpa.entity.NoticeEntity;
import org.myweb.first.notice.jpa.entity.QNoticeEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom {
    //QueryDSL 사용방법 첫번째 :
    //2. queryDSL 용 커스텀 인터페이스를 상속받은 후손 클래스를 만들고, 추상메소드를 오버라이딩한다.
    //  => 메소드 구현시 queryDSL 을 사용한다.

    //QueryDSL 에 대한 config 클래스를 먼저 만들고 나서 필드 선언함
    private final JPAQueryFactory queryFactory;     //이것만 선언하면 됨
    //반드시 final 사용할 것

    private final EntityManager entityManager;  //JPQL 사용을 위해 의존성 주입
    private QNoticeEntity notice = QNoticeEntity.noticeEntity;
    //notice 테이블을 의미하는 NoticeEntity 를 notice 로 표현한다는 선언임

    //마지막 공지글 번호 조회 (새 글 등록시 공지글번호 + 1 로 사용하기 위함)
    @Override
    public int findLastNoticeNo() {
        NoticeEntity noticeEntity = queryFactory
                .selectFrom(notice)         //select * from notice
                .orderBy(notice.noticeNo.desc())
                .fetch().get(0);    //내림차순정렬한 공지목록의 첫번째 행 선택 == 가장 최근 등록된 공지글
        return noticeEntity.getNoticeNo();
    }

    //검색 관련 메소드 ------------------------------------
    @Override
    public List<NoticeEntity> findSearchTitle(String keyword, Pageable pageable) {
        return queryFactory
                .selectFrom(notice)
                .where(notice.noticeTitle.like("%" + keyword + "%"))
                .orderBy(notice.noticeNo.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countSearchTitle(String keyword) {
        return queryFactory
                .selectFrom(notice)     //select * from notice
                .where(notice.noticeTitle.like("%" + keyword + "%"))  //where noticetitle %keyword%
                .fetchCount();
    }

    @Override
    public List<NoticeEntity> findSearchContent(String keyword, Pageable pageable) {
        return queryFactory
                .selectFrom(notice)
                .where(notice.noticeContent.like("%" + keyword + "%"))
                .orderBy(notice.noticeNo.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countSearchContent(String keyword) {
        return queryFactory
                .selectFrom(notice)     //select * from notice
                .where(notice.noticeContent.like("%" + keyword + "%"))  //where noticetitle %keyword%
                .fetchCount();
    }

    @Override
    public List<NoticeEntity> findSearchDate(Date begin, Date end, Pageable pageable) {
        return queryFactory
                .selectFrom(notice)
                .where(notice.noticeDate.between(begin, end))
                .orderBy(notice.noticeNo.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countSearchDate(Date begin, Date end) {
        return queryFactory
                .selectFrom(notice)     //select * from notice
                .where(notice.noticeDate.between(begin, end))  //where noticedate between :begin and :end
                .fetchCount();
    }

}
