package org.myweb.first.board.jpa.repository;

import org.myweb.first.board.jpa.entity.ReplyEntity;

import java.util.List;

//QueryDSL 사용방법 첫번째 :
//1. querydsl 용 커스텀 리포지토리 인터페이스를 만들고, 추가하는 메소드들을 추상메소드로 선언한다.
public interface ReplyRepositoryCustom {
    //같은 원글에 대한 댓글과 대댓글들 조회
     List<ReplyEntity> findAllReply(int boardNum);
    //같은 원글에 대한 댓글과 대댓글 모두 조회수 1증가 처리
     void addReadCount(int boardRef);
     int findLastReplyNum();
     int countReplySeq(int boardRef, int replyLev);
     int findLastReplySeq(int boardRef, int replyLev);
     int countReplyReplySeq(int boardRef, int replyReplyRef, int replyLev);
     int findLastReplyReplySeq(int boardRef, int replyReplyRef, int replyLev);

    int updateReply(ReplyEntity entity);
}
