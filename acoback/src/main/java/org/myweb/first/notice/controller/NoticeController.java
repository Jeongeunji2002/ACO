package org.myweb.first.notice.controller;

import aj.org.objectweb.asm.commons.TryCatchBlockSorter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.common.FileNameChange;
import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;
import org.myweb.first.notice.model.dto.Notice;
import org.myweb.first.notice.model.service.NoticeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j    //log 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@RequiredArgsConstructor
@RestController
@RequestMapping("/notice")
@CrossOrigin			//다른 port 에서 오는 요청을 처리하기 위함
public class NoticeController {

	private final NoticeService noticeService;

	@Value("${file.upload-dir}")
	private String uploadDir;

	// 뷰 페이지 이동 처리용 메소드 ---------------------------------------
	//HomeController 로 코드 이동시킴

	// 요청 처리용 메소드 -----------------------------------------
	@GetMapping("/ntop3")
	public ArrayList<Notice> noticeNewTop3Method() {
		// 최근 등록된 공지글 3개 조회 요청함
		ArrayList<Notice> list = noticeService.selectTop3();
		return list;
	}

	// 공지글 상세 내용보기 요청 처리용
	@GetMapping("/detail/{noticeNo}")
	public ResponseEntity<Notice> noticeDetailMethod(@PathVariable int noticeNo) {
		log.info("ndetail.do : " + noticeNo); // 전송받은 값 확인

		Notice notice = noticeService.selectNotice(noticeNo);
		// 조회수 1증가 처리
		noticeService.updateAddReadCount(noticeNo);

		return new ResponseEntity<>(notice, HttpStatus.OK);
	}

	// 공지사항 전체 목록보기 요청 처리용 (페이징 처리 : 한 페이지에 10개씩 출력 처리)
	@GetMapping
	public Map<String, Object> noticeListMethod(
			@RequestParam(name = "page", defaultValue = "1") int currentPage,
			@RequestParam(name = "limit", defaultValue = "10") int limit) {
		// page : 출력할 페이지, limit : 한 페이지에 출력할 목록 갯수

		// 총 목록갯수 조회해서 총 페이지 수 계산함
		int listCount = noticeService.selectListCount();
		// 페이지 관련 항목 계산 처리
		Paging paging = new Paging(listCount, limit, currentPage, "nlist.do");
		paging.calculate();

		//JPA 가 제공하는 메소드에 필요한 Pageable 객체 생성함 ---------------------------------------
		Pageable pageable = PageRequest.of(paging.getCurrentPage() - 1, paging.getLimit(),
				Sort.by(Sort.Direction.DESC, "noticeNo"));

		// 서비스롤 목록 조회 요청하고 결과 받기
		ArrayList<Notice> list = noticeService.selectList(pageable);

		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("paging", paging);

		return map;
	}

	// 새 공지글 등록 요청 처리용 (파일 업로드 기능 추가)
	@PostMapping
	public ResponseEntity noticeInsertMethod(
			@ModelAttribute Notice notice,
			@RequestParam(name = "ofile", required = false) MultipartFile mfile) throws IOException {
		log.info("notice insert : " + notice);

		// 공지사항 첨부파일 저장 폴더를 경로 지정
		//classpath 인 src/main/resources 아래에 저장 폴더를 둔 경우에 (war 만들면 사용못함)
		//String savePath = new ClassPathResource("notice_upfiles").getFile().getAbsolutePath();
		//D:\springboot_workspace\first_serverside\build\resources\main\static\notice_upfiles

		String savePath = uploadDir + "/notice";
		log.info("savePath : " + savePath);


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
			if (fileName != null && !fileName.isEmpty()) {
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

			// notice 객체에 첨부파일 정보 저장 처리
			notice.setOriginalFilePath(fileName);
			notice.setRenameFilePath(renameFileName);
		} // 첨부파일이 있을 때

		//중요도(importance) 가 null 일때 (체크되지 않았을 때)
		if(notice.getImportance() == null || notice.getImportance().equals("false")){
			notice.setImportance("N");
		}
		if(notice.getImportance().equals("true")){
			notice.setImportance("Y");
		}

		if (noticeService.insertNotice(notice) > 0) {
			// 새 공지글 등록 성공시 목록 페이지 내보내기 요청
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// 첨부파일 다운로드 요청 처리용 메소드
	@GetMapping("/nfdown")
	public ResponseEntity<Resource> fileDownload(
			@RequestParam("ofile") String originalFileName,
			@RequestParam("rfile") String renameFileName) throws IOException {

		// 공지사항 첨부파일 저장 폴더 경로 지정 => classpath 경로 설정
		// src/main/resources/notice_upfiles (war 만들면 사용 못 함)
		//String savePath = "/notice_upfiles/";
		//D:\springboot_workspace\first_serverside\build\resources\main\static\notice_upfiles

		String savePath = uploadDir + "/notice";

		//Path 객체 생성
		Path path = Paths.get(savePath).toAbsolutePath().normalize();

		// classpath 에서 파일 찾기
//		ClassPathResource classPathResource = new ClassPathResource(savePath + renameFileName);
//		log.info("nfdown : " + classPathResource.getFile().getAbsolutePath());


//		if(!classPathResource.exists()) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//		}

		// Resource 객체 생성
		Resource resource = null;
		try {
			//resource = classPathResource;
			resource = new UrlResource(path.toUri() + "/" + renameFileName);
		} catch (Exception e){
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		// 파일 이름 설정 (한글 파일명 때문에 인코딩)
		String encodedFileName = originalFileName != null ? originalFileName : renameFileName;
		try {
			encodedFileName = URLEncoder.encode(encodedFileName, "UTF-8").replaceAll("\\+", "%20");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		// Content-Disposition 해더 설정
		String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Disposition", contentDisposition)
				.body(resource);
	}


	// 공지글 삭제 요청 처리용
	@DeleteMapping("/{noticeNo}")
	public ResponseEntity noticeDeleteMethod(@PathVariable int noticeNo,
			@RequestParam(name = "rfile", required = false) String renameFileName) {
		if (noticeService.deleteNotice(noticeNo) > 0) { // 공지글 삭제 성공시
			// 공지글 삭제 성공시 저장 폴더에 있는 첨부파일도 삭제 처리함
			if (renameFileName != null && renameFileName.length() > 0) {
				// 공지사항 첨부파일 저장 폴더 경로 지정
				String savePath = uploadDir + "/notice";

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

	// 공지글 수정 요청 처리용 (파일 업로드 기능 사용)
	@PutMapping("/{no}")
	public ResponseEntity noticeUpdateMethod(
			@ModelAttribute Notice notice,
			@RequestParam(name = "ofile", required = false) MultipartFile mfile) {
		log.info("mupdate.do : " + notice); // 전송온 값 확인

		// 중요도 체크 안 한 경우 처리
		if (notice.getImportance() == null) {
			if(notice.getImportance().equals("false")) {
				notice.setImportance("N");
				notice.setImpEndDate(new Date(System.currentTimeMillis())); // 오늘 날짜를 기본 날짜로 지정함
			}
			if(notice.getImportance().equals("true")) {
				notice.setImportance("Y");
			}
		}

		//수정 날짜도 변경
		notice.setNoticeDate(new Date(System.currentTimeMillis()));

		// 첨부파일 관련 변경 사항 처리
		// 공지사항 첨부파일 저장 폴더 경로 지정
		String savePath = uploadDir + "/notice";
		log.info("savePath : " + savePath);

		// 새로운 첨부파일로 변경 업로드된 경우
		// => 이전 파일과 파일정보 삭제함
		if(mfile != null && !mfile.isEmpty()) {
			// 저장 폴더에서 이전 파일은 삭제함
			new File(savePath, notice.getRenameFilePath()).delete();
			// notice 안의 파일 정보도 삭제함
			notice.setOriginalFilePath(null);
			notice.setRenameFilePath(null);

			// 전송온 파일이름 추출함
			String fileName = mfile.getOriginalFilename();
			String renameFileName = null;

			// 파일 이름바꾸기함 : 년월일시분초.확장자
			if (fileName != null && fileName.length() > 0) {
				// 바꿀 파일명에 대한 문자열 만들기
				renameFileName = FileNameChange.change(fileName, "yyyyMMddHHmmss");

				try {
					// 저장 폴더에 파일명 바꾸어 저장하기
					mfile.transferTo(new File(savePath, renameFileName));
				} catch (Exception e) {
					e.printStackTrace();
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
				}
			} // 파일명 바꾸기와 저장 처리

			// notice 객체에 첨부파일 정보 저장 처리
			notice.setOriginalFilePath(fileName);
			notice.setRenameFilePath(renameFileName);
		} // 새로운 첨부파일이 있을 때

		if (noticeService.updateNotice(notice) > 0) { // 공지글 수정 성공시
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// 공지글 제목 검색용 (페이징 처리 포함)
	@GetMapping("/search/title")
	public ResponseEntity<Map> noticeSearchTitleMethod(
			@RequestParam("action") String action,
			@RequestParam("keyword") String keyword,
			@RequestParam(name = "page", defaultValue = "1") int currentPage,
			@RequestParam(name = "limit", defaultValue = "10") int limit) {
		// page : 출력할 페이지, limit : 한 페이지에 출력할 목록 갯수

		// 검색결과가 적용된 총 목록갯수 조회해서 총 페이지 수 계산함
		int listCount = noticeService.selectSearchTitleCount(keyword);
		// 페이지 관련 항목 계산 처리
		Paging paging = new Paging(listCount, limit, currentPage, "nsearchTitle.do");
		paging.calculate();

		//JPA 가 제공하는 메소드에 필요한 Pageable 객체 생성함 ---------------------------------------
		Pageable pageable = PageRequest.of(paging.getCurrentPage() - 1, paging.getLimit(),
				Sort.by(Sort.Direction.DESC, "noticeNo"));

		// 서비스롤 목록 조회 요청하고 결과 받기
		ArrayList<Notice> list = noticeService.selectSearchTitle(keyword, pageable);
		Map<String, Object> map = new HashMap<String, Object>();

		if (list != null && list.size() > 0) {
			map.put("list", list);
			map.put("paging", paging);
			//아래 데이터들은 요청한 페이지에 존재하는 값이므로 응답 처리에서 제외함
//			map.put("currentPage", currentPage);
			map.put("action", action);
			map.put("keyword", keyword);

			return new ResponseEntity<Map>(map, HttpStatus.OK);
		} else {
			map.put("message", "검색실패");
			return new ResponseEntity<Map>(map, HttpStatus.BAD_REQUEST);
		}
	}

	// 공지글 내용 검색용 (페이징 처리 포함)
	@GetMapping("/search/content")
	public ResponseEntity<Map> noticeSearchContentMethod(
			 @RequestParam("action") String action,
			 @RequestParam("keyword") String keyword,
			 @RequestParam(name = "page", defaultValue = "1") int currentPage,
			 @RequestParam(name = "limit", defaultValue = "10") int limit) {

		// page : 출력할 페이지, limit : 한 페이지에 출력할 목록 갯수
		// 검색결과가 적용된 총 목록갯수 조회해서 총 페이지 수 계산함
		int listCount = noticeService.selectSearchContentCount(keyword);
		// 페이지 관련 항목 계산 처리
		Paging paging = new Paging(listCount, limit, currentPage, "nsearchContent.do");
		paging.calculate();

		//JPA 가 제공하는 메소드에 필요한 Pageable 객체 생성함 ---------------------------------------
		Pageable pageable = PageRequest.of(paging.getCurrentPage() - 1, paging.getLimit(),
				Sort.by(Sort.Direction.DESC, "noticeNo"));

		// 서비스롤 목록 조회 요청하고 결과 받기
		ArrayList<Notice> list = noticeService.selectSearchContent(keyword, pageable);
		Map<String, Object> map = new HashMap<String, Object>();

		if (list != null && list.size() > 0) {
			map.put("list", list);
			map.put("paging", paging);
			//아래 데이터들은 요청한 페이지에 존재하는 값이므로 응답 처리에서 제외함
//			map.put("currentPage", currentPage);
//			map.put("action", action);
//			map.put("keyword", keyword);

			return new ResponseEntity<Map>(map, HttpStatus.OK);
		} else {
			map.put("message", "검색실패");
			return new ResponseEntity<Map>(map, HttpStatus.BAD_REQUEST);
		}
	}

	// 공지글 등록날짜 검색용 (페이징 처리 포함)
	@GetMapping("/search/date")
	public ResponseEntity<Map> noticeSearchDateMethod(
			Search search,
			@RequestParam("action") String action,
			@RequestParam(name = "page", defaultValue = "1") int currentPage,
			@RequestParam(name = "limit", defaultValue = "10") int limit) {

		// page : 출력할 페이지, limit : 한 페이지에 출력할 목록 갯수

		// 검색결과가 적용된 총 목록갯수 조회해서 총 페이지 수 계산함
		int listCount = noticeService.selectSearchDateCount(search);
		// 페이지 관련 항목 계산 처리
		Paging paging = new Paging(listCount, limit, currentPage, "nsearchDate.do");
		paging.calculate();

		//JPA 가 제공하는 메소드에 필요한 Pageable 객체 생성함 ---------------------------------------
		Pageable pageable = PageRequest.of(paging.getCurrentPage() - 1, paging.getLimit(),
				Sort.by(Sort.Direction.DESC, "noticeNo"));

		// 서비스롤 목록 조회 요청하고 결과 받기
		ArrayList<Notice> list = noticeService.selectSearchDate(search, pageable);
		Map<String, Object> map = new HashMap<String, Object>();

		if (list != null && list.size() > 0) {
			map.put("list", list);
			map.put("paging", paging);
			//아래 데이터들은 요청한 페이지에 존재하는 값이므로 응답 처리에서 제외함
//			map.put("currentPage", currentPage);
//			map.put("action", action);
//			map.put("begin", search.getBegin());
//			map.put("end", search.getEnd());

			return new ResponseEntity<Map>(map, HttpStatus.OK);
		} else {
			map.put("message", "검색실패");
			return new ResponseEntity<Map>(map, HttpStatus.BAD_REQUEST);
		}
	}
}
