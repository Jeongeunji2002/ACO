package org.myweb.first.board.jpa.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.myweb.first.board.jpa.entity.QReplyEntity;
import org.myweb.first.board.jpa.entity.ReplyEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReplyRepositoryCustomImpl implements ReplyRepositoryCustom {
    //QueryDSL 사용방법 3 :
    //상속, 재구현(implement) 없는 queryDSL 만으로 구성하는 리포지터리 클래스 작성 방법임

    //QueryDSL 에 대한 config 클래스를 먼저 만들고 나서 필드 선언함
    private final JPAQueryFactory queryFactory;     //이것만 선언하면 됨
    //반드시 final 사용할 것

    private final EntityManager entityManager;  //JPQL 사용을 위해 의존성 주입
    private QReplyEntity reply = QReplyEntity.replyEntity;
    //reply 테이블을 의미하는 ReplyEntity 를 reply 로 표현한다는 선언임

    //같은 원글에 대한 댓글과 대댓글들 조회
    @Override
    public List<ReplyEntity> findAllReply(int boardNum) {
        return queryFactory
                .selectFrom(reply)
                .where(reply.boardRef.eq(boardNum))
                .orderBy(reply.replyReplyRef.desc(), reply.replyLev.asc(), reply.replySeq.desc())
                .fetch();
    }

    //같은 원글에 대한 댓글과 대댓글 모두 조회수 1증가 처리
    @Override
    public void addReadCount(int boardRef) {
        queryFactory
                .update(reply)
                .set(reply.replyReadCount, reply.replyReadCount.add(1))
                .where(reply.boardRef.eq(boardRef))
                .execute();
        entityManager.flush();
        entityManager.clear();
    }

    @Override
    public int findLastReplyNum() {
        return queryFactory
                .selectFrom(reply)
                .orderBy(reply.replyNum.desc())
                .fetch().get(0).getReplyNum();  //가장 마지막 등록 글 1개 조회
    }

    @Override
    public int countReplySeq(int boardRef, int replyLev) {
        return (int)queryFactory
                .selectFrom(reply)
                .where(reply.boardRef.eq(boardRef).and(reply.replyLev.eq(replyLev)))
                .fetchCount();
    }

    @Override
    public int findLastReplySeq(int boardRef, int replyLev) {
        return queryFactory
                .selectFrom(reply)
                .where(reply.boardRef.eq(boardRef).and(reply.replyLev.eq(replyLev)))
                .fetch().get(0).getReplySeq();
    }

    @Override
    public int countReplyReplySeq(int boardRef, int replyReplyRef, int replyLev) {
        return (int)queryFactory
                .selectFrom(reply)
                .where(reply.boardRef.eq(boardRef).and(reply.replyReplyRef.eq(replyReplyRef).and(reply.replyLev.eq(replyLev))))
                .fetchCount();
    }

    @Override
    public int findLastReplyReplySeq(int boardRef, int replyReplyRef, int replyLev) {
        return queryFactory
                .selectFrom(reply)
                .where(reply.boardRef.eq(boardRef).and(reply.replyReplyRef.eq(replyReplyRef).and(reply.replyLev.eq(replyLev))))
                .fetch().get(0).getReplySeq();
    }

    @Override
    public int updateReply(ReplyEntity entity) {
        int result = (int)queryFactory
                        .update(reply)
                        .set(reply.replyTitle, entity.getReplyTitle()) // replyTitle 수정
                        .set(reply.replyContent, entity.getReplyContent()) // replyContent 수정
                        .where(reply.replyNum.eq(entity.getReplyNum())) // 조건: replyNum 일치
                        .execute();

        entityManager.flush();
        entityManager.clear();

        return result;
    }

}
