package org.myweb.first.board.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.board.model.dto.Board;
import org.myweb.first.board.model.dto.Reply;
import org.myweb.first.board.model.service.BoardService;
import org.myweb.first.board.model.service.ReplyService;
import org.myweb.first.common.FileNameChange;
import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Slf4j    //log 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@RequiredArgsConstructor
@RestController
@RequestMapping("/board")
@CrossOrigin			//다른 port 에서 오는 요청을 처리하기 위함
public class BoardController {

	@Autowired
	private BoardService boardService;

	@Autowired
	private ReplyService replyService;

	@Value("${file.upload-dir}")
	private String uploadDir;

	// 뷰 페이지 이동 처리용 메소드 ---------------------------------------
	// RestController 에서는 뷰페이지 이동 처리 못 하므로, HomeController 로 이동 처리

	// 요청 처리용 메소드 ---------------------------------------------------

	// ajax 요청 : 조회수 많은 인기 게시글 top-3 요청 처리용
	@GetMapping("/btop3")
	public ArrayList<Board> boardTop3Method() {
		// 조회수 많은 게시글 3개 조회 요청함
		return boardService.selectTop3();
	} // btop3

	// 게시글 전체 목록보기 요청 처리용 (페이징 처리 : 한 페이지에 10개씩 출력 처리)
	@GetMapping
	public Map<String, Object> boardListMethod(
			@RequestParam(name = "page", defaultValue = "1") int currentPage,
			@RequestParam(name = "limit", defaultValue = "10") int limit) {
		// page : 출력할 페이지, limit : 한 페이지에 출력할 목록 갯수

		// 총 목록갯수 조회해서 총 페이지 수 계산함
		int listCount = boardService.selectListCount();
		// 페이지 관련 항목 계산 처리
		Paging paging = new Paging(listCount, limit, currentPage, "blist.do");
		paging.calculate();

		//JPA 에 사용될 Pageable 객체 생성
		Pageable pageable = PageRequest.of(
				paging.getCurrentPage() - 1,
				paging.getLimit(),
				Sort.by(Sort.Direction.DESC, "boardNum"));

		// 서비스롤 목록 조회 요청하고 결과 받기		
		ArrayList<Board> list = boardService.selectList(pageable);
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("paging", paging);

		return map;
	}

	// 게시글 원글 상세 내용보기 요청 처리용
	@GetMapping("/detail/{boardNum}")
	public ResponseEntity<Map> boardDetailMethod(@PathVariable int boardNum) {
		log.info("bdetail.do : " + boardNum); // 전송받은 값 확인

		Board board = boardService.selectBoard(boardNum);
		// 조회수 1증가 처리
		boardService.updateAddReadCount(boardNum);

		//해당 원글에 대한 댓글과 대댓글도 조회해 옴
		ArrayList<Reply> replyList = replyService.selecReplyList(boardNum);
		//해당 원글에 대한 댓글 & 대댓글 조회수 1증가 처리함
		replyService.updateAddReadCount(boardNum);

		Map<String, Object> map = new HashMap<>();
		map.put("board", board);
		map.put("list", replyList);

		return new ResponseEntity<>(map, HttpStatus.OK);
	} // /bdetail/no

	// 첨부파일 다운로드 요청 처리용 메소드
	@GetMapping("/bfdown")
	public ResponseEntity<Resource> filedownMethod(
			@RequestParam("ofile") String originalFileName,
			@RequestParam("rfile") String renameFileName) {

		// 게시글 첨부파일 저장 폴더 경로 지정
		String savePath = uploadDir + "/board";

		// 저장 폴더에서 읽을 파일에 대한 File 객체 생성
		File downFile = new File(savePath + File.separator + renameFileName);
		if (!downFile.exists()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			//return new ResponseEntity.status(HttpStatus.NOT_FOUND);  같음
		}

		// Resource 객체 생성
		Resource resource = new FileSystemResource(downFile);

		// 파일 이름 설정
		String encodedFileName = originalFileName != null ? originalFileName : downFile.getName();
		try {
			encodedFileName = URLEncoder.encode(encodedFileName, "UTF-8").replaceAll("\\+", "%20");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		// Content-Disposition 헤더 설정
		String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Disposition", contentDisposition)
				.body(resource);
	} // bfdown

	// 새 게시 원글 등록 요청 처리용 (파일 업로드 기능 추가)
	@PostMapping()
	public ResponseEntity boardInsertMethod(
			@ModelAttribute Board board,
			@RequestParam(name = "ofile", required = false) MultipartFile mfile) {
		log.info("boardInsertMethod : " + board); // 전송온 값 저장 확인

		// 게시 원글 첨부파일 저장 폴더를 경로 지정
		String savePath = uploadDir + "/board";

		File directory = new File(savePath);
		if (!directory.exists()) {
			// 폴더가 없으면 새로 폴더 만들기함
			directory.mkdirs();
		}

		// 첨부파일이 있을 때
		if (mfile != null && !mfile.isEmpty()) {
			// 전송온 파일이름 추출함
			String fileName = mfile.getOriginalFilename();
			String renameFileName = null;

			// 저장폴더에는 변경된 이름을 저장 처리함
			// 파일 이름바꾸기함 : 년월일시분초.확장자
			if (fileName != null && fileName.length() > 0) {
				// 바꿀 파일명에 대한 문자열 만들기
				renameFileName = FileNameChange.change(fileName, "yyyyMMddHHmmss");
				// 바뀐 파일명 확인
				log.info("첨부파일명 확인 : " + renameFileName);

				try {
					// 저장 폴더에 파일명 바꾸어 저장하기
					mfile.transferTo(new File(savePath, renameFileName));
				} catch (Exception e) {
					e.printStackTrace();
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
				}
			} // 파일명 바꾸기

			// board 객체에 첨부파일 정보 저장 처리
			board.setBoardOriginalFilename(fileName);
			board.setBoardRenameFilename(renameFileName);
		} // 첨부파일이 있을 때

		if (boardService.insertBoard(board) > 0) {
			// 새 게시 원글 등록 성공시 목록 페이지 내보내기 요청
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	} // binsert.do

	// 게시글 원글 삭제 요청 처리용
	// 원글 삭제시 외래키(foreign key) 제약조건의 삭제룰(on delete cascade)에 의해 댓글과 대댓글도 함께 삭제됨
	@DeleteMapping("/{boardNum}")
	public ResponseEntity boardDeleteMethod(
			@PathVariable int boardNum,
			@RequestParam(name = "rfile", required = false) String renameFileName
			) {
		Board board = new Board();
		board.setBoardNum(boardNum);

		if (boardService.deleteBoard(board) > 0) { // 공지글 삭제 성공시
			// 공지글 삭제 성공시 저장 폴더에 있는 첨부파일도 삭제 처리함
			if (renameFileName != null && renameFileName.length() > 0) {
				// 공지사항 첨부파일 저장 폴더 경로 지정
				String savePath = uploadDir + "/board";

				//Path 객체 생성
				Path path = Paths.get(savePath, renameFileName);
				File file = path.toFile();
				if (file.exists()) {
					if(!file.delete()){
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
					}
				}
			}
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// 게시 원글 수정 요청 처리용 (파일 업로드 기능 사용)
	@PutMapping("/{no}")
	public ResponseEntity originUpdateMethod(
			@ModelAttribute Board board,
			@RequestParam(name = "ofile", required = false) MultipartFile mfile) {
		log.info("originUpdateMethod : " + board); // 전송온 값 확인

		// 첨부파일 관련 변경 사항 처리
		// 게시원글 첨부파일 저장 폴더 경로 지정
		String savePath = uploadDir + "/board";

		// 새로운 첨부파일이 있을 때 => 이전 파일과 파일정보 삭제함
		if (mfile != null && !mfile.isEmpty()) {
			// 전송온 파일이름 추출함
			String fileName = mfile.getOriginalFilename();
			String renameFileName = null;

			// 기존의 첨부파일은 제거함
			if(board.getBoardRenameFilename() != null && board.getBoardRenameFilename().length() > 0) {
				new File(savePath, board.getBoardRenameFilename()).delete();
			}

			// 저장폴더에는 변경된 이름을 저장 처리함
			// 파일 이름바꾸기함 : 년월일시분초.확장자
			if (fileName != null && fileName.length() > 0) {
				// 바꿀 파일명에 대한 문자열 만들기
				renameFileName = FileNameChange.change(fileName, "yyyyMMddHHmmss");
				// 바뀐 파일명 확인
				log.info("첨부파일명 확인 : " + renameFileName);

				try {
					// 저장 폴더에 파일명 바꾸어 저장하기
					mfile.transferTo(new File(savePath, renameFileName));
				} catch (Exception e) {
					e.printStackTrace();
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
				}
			} // 파일명 바꾸기

			// board 객체에 변경된 첨부파일 정보 저장 처리
			board.setBoardOriginalFilename(fileName);
			board.setBoardRenameFilename(renameFileName);
		} // 첨부파일이 있을 때

		//현재 날짜를 게시글 등록 날짜로 수정한다면
		board.setBoardDate(new java.sql.Date(System.currentTimeMillis()));

		if (boardService.updateOrigin(board) > 0) { // 게시원글 수정 성공시
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// *****************************************************************
	// 공지글 제목 검색용 (페이징 처리 포함)
	@GetMapping("/search/title")
	public ResponseEntity<Map> boardSearchTitleMethod(
			@RequestParam("action") String action,
			@RequestParam("keyword") String keyword,
			@RequestParam(name = "page", defaultValue = "1") int currentPage,
			@RequestParam(name = "limit", defaultValue = "10") int limit) {

		// page : 출력할 페이지, limit : 한 페이지에 출력할 목록 갯수

		// 검색결과가 적용된 총 목록갯수 조회해서 총 페이지 수 계산함
		int listCount = boardService.selectSearchTitleCount(keyword);
		// 페이지 관련 항목 계산 처리
		Paging paging = new Paging(listCount, limit, currentPage, "bsearchTitle.do");
		paging.calculate();

		//JPA 가 사용할 Pageable 객체 생성
		Pageable pageable = PageRequest.of(paging.getCurrentPage() - 1,
				paging.getLimit(), Sort.by(Sort.Direction.DESC, "boardNum"));

		// 서비스롤 목록 조회 요청하고 결과 받기
		ArrayList<Board> list = boardService.selectSearchTitle(keyword, pageable);
		Map<String, Object> map = new HashMap<String, Object>();

		if (list != null && list.size() > 0) {
			map.put("list", list);
			map.put("paging", paging);

			return new ResponseEntity<Map>(map, HttpStatus.OK);
		} else {
			map.put("message", action + "에 대한 " + keyword + " 검색 결과가 존재하지 않습니다.");
			return new ResponseEntity<Map>(map, HttpStatus.BAD_REQUEST);
		}
	}

	// 게시글 작성자 검색용 (페이징 처리 포함)
	@GetMapping("/search/writer")
	public ResponseEntity<Map> boardSearchWriterMethod(
			@RequestParam("action") String action,
			@RequestParam("keyword") String keyword,
			@RequestParam(name = "page", defaultValue = "1") int currentPage,
			@RequestParam(name = "limit", defaultValue = "10") int limit) {

		// page : 출력할 페이지, limit : 한 페이지에 출력할 목록 갯수

		// 검색결과가 적용된 총 목록갯수 조회해서 총 페이지 수 계산함
		int listCount = boardService.selectSearchWriterCount(keyword);
		// 페이지 관련 항목 계산 처리
		Paging paging = new Paging(listCount, limit, currentPage, "bsearchWriter.do");
		paging.calculate();

		//JPA 가 사용할 Pageable 객체 생성
		Pageable pageable = PageRequest.of(paging.getCurrentPage() - 1,
				paging.getLimit(), Sort.by(Sort.Direction.DESC, "boardNum"));

		// 서비스롤 목록 조회 요청하고 결과 받기
		ArrayList<Board> list = boardService.selectSearchWriter(keyword, pageable);
		Map<String, Object> map = new HashMap<String, Object>();

		if (list != null && list.size() > 0) {
			map.put("list", list);
			map.put("paging", paging);

			return new ResponseEntity<Map>(map, HttpStatus.OK);
		} else {
			map.put("message", action + "에 대한 " + keyword + " 검색 결과가 존재하지 않습니다.");
			return new ResponseEntity<Map>(map, HttpStatus.BAD_REQUEST);
		}
	}

	// 게시 원글 등록날짜 검색용 (페이징 처리 포함)
	@GetMapping("/search/date")
	public ResponseEntity<Map> boardSearchDateMethod(
			Search search,
			@RequestParam("action") String action,
			@RequestParam(name = "page", defaultValue = "1") int currentPage,
			@RequestParam(name = "limit", defaultValue = "10") int limit) {

		// page : 출력할 페이지, limit : 한 페이지에 출력할 목록 갯수

		// 검색결과가 적용된 총 목록갯수 조회해서 총 페이지 수 계산함
		int listCount = boardService.selectSearchDateCount(search);
		// 페이지 관련 항목 계산 처리
		Paging paging = new Paging(listCount, limit, currentPage, "bsearchDate.do");
		paging.calculate();

		//JPA 가 사용할 Pageable 객체 생성
		Pageable pageable = PageRequest.of(paging.getCurrentPage() - 1,
				paging.getLimit(), Sort.by(Sort.Direction.DESC, "boardNum"));

		// 서비스롤 목록 조회 요청하고 결과 받기
		ArrayList<Board> list = boardService.selectSearchDate(search, pageable);
		Map<String, Object> map = new HashMap<String, Object>();

		if (list != null && list.size() > 0) {
			map.put("list", list);
			map.put("paging", paging);

			return new ResponseEntity<Map>(map, HttpStatus.OK);
		} else {
			map.put("message", action + "에 대한 " + search.getBegin() + "~" + search.getEnd() + " 검색 결과가 존재하지 않습니다.");
			return new ResponseEntity<Map>(map, HttpStatus.BAD_REQUEST);
		}
	}
}
