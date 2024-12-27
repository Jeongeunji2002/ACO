package org.myweb.first.board.jpa.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.myweb.first.board.jpa.entity.BoardEntity;
import org.myweb.first.board.jpa.entity.QBoardEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {
    //QueryDSL 사용방법 첫번째 :
    //2. querydsl 용 커스텀 인터페이스를 상속받아, 추상메소드 오버라이딩 구현한다.

    //QueryDSL 에 대한 config 클래스를 먼저 만들고 나서 필드 선언함
    private final JPAQueryFactory queryFactory;     //이것만 선언하면 됨
    //반드시 final 사용할 것

    private final EntityManager entityManager;  //JPQL 사용을 위해 의존성 주입
    private QBoardEntity board = QBoardEntity.boardEntity;
    //board 테이블을 의미하는 BoardEntity 를 board 로 표현한다는 선언임

    @Override
    public List<Object[]> findTop3() {
        //주의 : querydsl 에서는 select 절과 where 절에서의 서브쿼리는 지원하지만,
        //          from 절에서의 서브쿼리를 지원하지 않는다. => 쿼리를 나눠서 실행 또는 조인 등으로 해결 필요함
        //JPQL 도 where 절과 group by 절에서만 서브쿼리 사용 가능함 => from 절에서 서브쿼리 사용 못함
        return entityManager.createNativeQuery(
                "SELECT BOARD_NUM, BOARD_TITLE, BOARD_WRITER, BOARD_READCOUNT  FROM BOARD ORDER BY BOARD_READCOUNT DESC")
                .getResultList();
        //EntityManager.createNativeQuery()로 SQL 쿼리를 실행하고, 결과를 List<Object[]> 형태로 반환함
    }

    @Override
    public int findLastBoardNum() {
        BoardEntity boardEntity = queryFactory
                .select(board)
                .from(board)
                .orderBy(board.boardNum.desc())
                .fetch().get(0);  //가장 마지막 등록 글 1개 조회
        return boardEntity.getBoardNum();
    }

    @Override
    public long countSearchTitle(String keyword) {
        return queryFactory
                .selectFrom(board)
                .where(board.boardTitle.like("%" + keyword + "%"))
                .fetchCount();
    }

    @Override
    public long countSearchWriter(String keyword) {
        return queryFactory
                .selectFrom(board)
                .where(board.boardWriter.like("%" + keyword + "%"))
                .fetchCount();
    }

    @Override
    public long countSearchDate(Date begin, Date end) {
        return queryFactory
                .selectFrom(board)
                .where(board.boardDate.between(begin, end))
                .fetchCount();
    }

    @Override
    public List<BoardEntity> findSearchTitle(String keyword, Pageable pageable) {
        return queryFactory
                .selectFrom(board)
                .where(board.boardTitle.like("%" + keyword + "%"))
                .orderBy(board.boardNum.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<BoardEntity> findSearchWriter(String keyword, Pageable pageable) {
        return queryFactory
                .selectFrom(board)
                .where(board.boardWriter.like("%" + keyword + "%"))
                .orderBy(board.boardNum.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<BoardEntity> findSearchDate(Date begin, Date end, Pageable pageable) {
        return queryFactory
                .selectFrom(board)
                .where(board.boardDate.between(begin, end))
                .orderBy(board.boardNum.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }



}
