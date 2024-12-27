package org.myweb.first.board.jpa.repository;

import org.myweb.first.board.jpa.entity.BoardEntity;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.util.List;

// QueryDSL 사용방법 첫번째 :
//1. querydsl 용 커스텀 인터페이스를 만들고, 추가하는 메소드들을 추상메소드로 선언한다.
public interface BoardRepositoryCustom {
     List<Object[]> findTop3();
     int findLastBoardNum();
     long countSearchTitle(String keyword);
     long countSearchWriter(String keyword);
     long countSearchDate(Date begin, Date end);
     List<BoardEntity> findSearchTitle(String keyword, Pageable pageable);
     List<BoardEntity> findSearchWriter(String keyword, Pageable pageable);
     List<BoardEntity> findSearchDate(Date begin, Date end, Pageable pageable);
}
