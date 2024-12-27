package org.myweb.first.board.model.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.board.jpa.entity.BoardEntity;
import org.myweb.first.board.jpa.repository.BoardRepositoryCustomImpl;
import org.myweb.first.board.jpa.repository.BoardRepository;
import org.myweb.first.board.model.dto.Board;
import org.myweb.first.common.Search;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
	//QueryDSL 사용방법 첫번째 :
	//4. Service 에 리포지토리를 하나만 선언해서 사용한다.

	//jpa 가 제공하는 기본 메소드와 추가한 메소드 둘 다 사용 가능해 짐
	private final BoardRepository boardRepository;

	public ArrayList<Board> selectTop3() {
		//jpa 가 제공하는 findAll() 로 모두 조회해 와서, 3개만 추출하는 방법이 있을 것임 (NoticeService 참조)
		//필요할 경우 jpa 가 제공하는 메소드로는 해결이 안되는 기능을 메소드를 추가해서 사용할 수 있음
		//추가 작성한 메소드 사용

		List<Object[]> results = boardRepository.findTop3();

		//내림차순정렬이므로 상위 3개만 추출함
		ArrayList<Board> list = new ArrayList<>();
		for(int i = 0; i < 3; i++){
			//각 Object[] 배열의 데이터를 Board 객체에 매핑함
			Object[] row = results.get(i);
			// 각 row의 데이터를 Board 객체로 매핑
			Board board = new Board();
			board.setBoardNum(((BigDecimal) row[0]).intValue());  //SQL 쿼리 결과에서 숫자 타입이 BigDecimal로 반환됨
			board.setBoardTitle((String) row[1]);
			board.setBoardWriter((String) row[2]);
			//board.setBoardDate(new Date(((Timestamp) row[2]).getTime()));  //쿼리 결과의 날짜 필드가 Timestamp 타입으로 반환됨
			board.setBoardReadCount(((BigDecimal) row[3]).intValue());
			//log.info("BoardService board : " + board);
			list.add(board);  //매핑된 Board 객체를 ArrayList<Board>에 추가
		}

		return list;
	}


	public int selectListCount() {
		//jpa 가 제공 : count() 사용
		return (int)boardRepository.count();
	}


	public ArrayList<Board> selectList(Pageable pageable) {
		//jpa 가 제공 : findAll(Pageable) : Page<Entity>
		Page<BoardEntity> page = boardRepository.findAll(pageable);
		ArrayList<Board> list = new ArrayList<>();
		for(BoardEntity entity : page){
			list.add(entity.toDto());
		}
		return list;
	}


	public Board selectBoard(int boardNum) {
		//jpa 가 제공하는 메소드 사용 : findById(id로 지정한 프로퍼티변수의 값) : Optional<Entity> 리턴
		Optional<BoardEntity> optionalBoard = boardRepository.findById(boardNum);
		return optionalBoard.get().toDto();
	}

	@Transactional
	public int updateAddReadCount(int boardNum) {
		//jpa가 제공하는 메소드 사용 : save(Entity) : 성공 Entity, 실패(에러) null
		//update 쿼리문 : 전달되는 엔티티의 id 프로퍼티의 값이 존재하면 update 가 작동됨
		//jpa 의 save() 는 수정할 정보를 가진 엔티티를 넘겨줘야 함
		Optional<BoardEntity> optionalBoard = boardRepository.findById(boardNum);
		BoardEntity entity = optionalBoard.get();
		log.info("BoardService boardEntity : " + entity);  //db 테이블로 부터 조회해 온 게시글 정보 확인
		entity.setBoardReadCount(entity.getBoardReadCount() + 1);  //조회수 1 증가 처리
		try {
			boardRepository.save(entity);	//조회수 1증가된 정보를 가진 엔티티가 전달됨
			return 1;
		}catch (Exception e){
			log.error(e.getMessage());
			return 0;
		}
	}

	@Transactional
	public int insertBoard(Board board) {
		//추가한 메소드 사용 : 현재 마지막 게시글번호 조회용
		board.setBoardNum(boardRepository.findLastBoardNum() + 1);
		log.info("BoardService board insert : " + board);
		//jpa 가 제공하는 메소드 사용 : save(Entity) => 성공하면 Entity, 실패하면 null 임
		// => pk 에 해당되는 글번호가 테이블에 없으면 insert 문 실행함
		// => pk 에 해당되는 글번호가 테이블에 있으면 update 문 실행함
		try {
			boardRepository.save(board.toEntity());
			return 1;
		}catch (Exception e){
			log.error(e.getMessage());
			return 0;
		}
	}


	@Transactional
	public int deleteBoard(Board board) {
		try {
			boardRepository.deleteById(board.getBoardNum());  //jpa 제공
			return 1;
		}catch (Exception e){
			log.error(e.getMessage());
			return 0;
		}
	}


	@Transactional
	public int updateOrigin(Board board) {
		//jpa 가 제공하는 메소드 사용 : save(Entity) : Entity
		try {
			boardRepository.save(board.toEntity());
			return 1;
		}catch (Exception e){
			log.error(e.getMessage());
			return 0;
		}
	}


	public int selectSearchTitleCount(String keyword) {
		// jpa 가 제공하는 전체 목록 갯수 조회하는 count() 로는 해결이 안됨
		// 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
		return (int)boardRepository.countSearchTitle(keyword);
	}


	public int selectSearchWriterCount(String keyword) {
		// jpa 가 제공하는 전체 목록 갯수 조회하는 count() 로는 해결이 안됨
		// 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
		return (int)boardRepository.countSearchWriter(keyword);
	}


	public int selectSearchDateCount(Search search) {
		// jpa 가 제공하는 전체 목록 갯수 조회하는 count() 로는 해결이 안됨
		// 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
		return (int)boardRepository.countSearchDate(search.getBegin(), search.getEnd());
	}


	public ArrayList<Board> selectSearchTitle(String keyword, Pageable pageable) {
		//jpa 가 제공하는 findAll(pageable) 메소드가 있으나, 키워드도 함께 전달되는 메소드는 없음
		//추가해서 사용함 : BoardRepository 인터페이스에 메소드 추가함
		List<BoardEntity> page = boardRepository.findSearchTitle(keyword, pageable);
		ArrayList<Board> list = new ArrayList<>();
		for(BoardEntity entity : page){
			list.add(entity.toDto());
		}
		return list;
	}


	public ArrayList<Board> selectSearchWriter(String keyword, Pageable pageable) {
		//jpa 가 제공하는 findAll(pageable) 메소드가 있으나, 키워드도 함께 전달되는 메소드는 없음
		//추가해서 사용함 : BoardRepository 인터페이스에 메소드 추가함
		List<BoardEntity> page = boardRepository.findSearchWriter(keyword, pageable);
		ArrayList<Board> list = new ArrayList<>();
		for(BoardEntity entity : page){
			list.add(entity.toDto());
		}
		return list;
	}


	public ArrayList<Board> selectSearchDate(Search search, Pageable pageable) {
		//jpa 가 제공하는 findAll(pageable) 메소드가 있으나, 키워드도 함께 전달되는 메소드는 없음
		//추가해서 사용함 : BoardRepository 인터페이스에 메소드 추가함
		List<BoardEntity> page = boardRepository.findSearchDate(search.getBegin(), search.getEnd(), pageable);
		ArrayList<Board> list = new ArrayList<>();
		for(BoardEntity entity : page){
			list.add(entity.toDto());
		}
		return list;
	}
}
