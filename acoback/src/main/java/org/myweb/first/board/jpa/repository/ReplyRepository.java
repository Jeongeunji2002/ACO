package org.myweb.first.board.jpa.repository;

import org.myweb.first.board.jpa.entity.BoardNativeVo;
import org.myweb.first.board.jpa.entity.ReplyEntity;
import org.myweb.first.board.model.dto.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Integer>, ReplyRepositoryCustom {
    //jpa 가 제공하는 기본 메소드 사용에 필요함

    //QueryDSL 사용방법 첫번째 :
    //3. querydsl 용 커스텀 인터페이스를 상속에 추가한다.
}
