// src/pages/notice/noticeList.js  => 공지글 목록 출력 페이지
import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';  //page 에서 page 바꾸기할 때 사용
import apiClient from '../../utils/axios';
import styles from './NoticeList.module.css';  //공지글 목록 출력 페이지에만 적용할 스타일시트 파일
import PagingView from '../../components/common/PagingView';  //PagingView 컴포넌트 임포트
import { AuthContext } from '../../AuthProvider';  // AuthProvider 에서 가져오기 위해 임포트

// Header.js 에서 받은 list 와 paging 정보르 받아서 랜더링하도록 코드 수정함
function NoticeList({ searchResults }) {
  const [notices, setNotices] = useState([]); // 공지사항 데이터를 저장할 상태
  //notices : 공지글 목록 저장용 변수
  //setNotices : 공지글 목록 저장 처리용 함수
  //useState([]) : 초기화 처리 함수, noties = []; 로 초기화선언 담당

  const [pagingInfo, setPagingInfo] = useState({
    currentPage: 1,
    maxPage: 1,
    startPage: 1,
    endPage: 1,
  });

  //현재 동작 상태 관리 (list or search)
  const [isSearchMode, setIsSearchMode] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null); // 에러 상태 관리
  //error : 에러 상태값 저장용 변수
  //setError : error 변수 값 변경하는 함수
  //useState(null) : error = null; 초기화선언함

  const { role } = useContext(AuthContext);  //AuthProvider 에서 가져오기

  const navigate = useNavigate();  //페이지 이동을 위한 navigate 함수 선언함
   /*
	리액트에서 만드는 함수 :
	//컴포넌트 작성시 사용하는 함수
	function 컴포넌트함수명(매개변수, 매개변수, ....){
		return (
			<element 속성={매개변수} 속성=값>내용</element>
		);
	}
	export default 컴포넌트함수명;

	//동작이나 기능을 처리하는 함수
	const 함수명 = (매개변수, 매개변수, .....) => {
		함수가 처리할 내용 코드 작성
	};
	//이 작성방식은 자바스크립트의 이름없는(anonymus) 함수와 같은 동작 방식임
	//함수명 = function(){}; 
  */

  // 서버에서 공지사항 목록 (기본 1page) 데이터를 가져오는 함수
  const fetchNotices = async (page) => {
    try {
      setLoading(true); // 로딩 상태 시작
      const response = await apiClient.get(`/notice?page=${page}`); // Spring Boot 서버 URL
      setNotices(response.data.list); // 응답 데이터를 상태로 설정  //notices = response.data.list; 과 같음
      setPagingInfo(response.data.paging);  //서버에서 제공하는 페이징 정보
      console.log(response.data.paging);
      setIsSearchMode(false);  // 일반 목록 조회 모드 지정
    } catch (err) {
      setError('공지사항을 불러오는 데 실패했습니다.'); // 에러 메시지 설정
	//error = '공지사항을 불러오는 데 실패했습니다.'; 과 같음
    } finally {
      setLoading(false); // 로딩 상태 종료, loading = false; 과 같음
    }
  };

  //검색 결과가 변경되면 상태 업데이트 코드 추가함함 
  // 컴포넌트가 처음 렌더링될 때 fetchNotices 호출
  // window.onload 될때 (jquery.document.ready 과 같음)
  useEffect(() => {
    if(searchResults){
      //검색 결과가 전달되면 검색 모드로 전환 처리함      
      setNotices(searchResults.list || []);
      setPagingInfo(searchResults.paging || {});
      setIsSearchMode(true);   //검색 모드로 설정
      setLoading(false);
    }else{
      // 초기 로드 또는 일반 조회회
      fetchNotices(1);
    }
  }, [searchResults]);

  //페이지 변경 핸들러 : 클릭한 page 의 목록을 요청 처리함 (일반 목록 페이지 요청 또는 검색 목록 페이지 요청)
  const handlePageChange = async (page) => {
    try{
      setLoading(true);
      if(isSearchMode){
        // 검색 목록 페이지 요청
        const response = await apiClient.get(`/notice/search/title`, {
          params: { action: searchResults.action, keyword: searchResults.keyword, page }
        })
        setNotices(response.data.list || []);
        setPagingInfo(response.data.paging || {});
      } else {
        fetchNotices(page);  //일반 목록 페이지 요청
      }
    } catch (error){
      setError('페이징 요청 실패!');
    } finally {
      setLoading(false);
    }
    
  };

  // 목록 버튼 클릭시 작동할 함수 (핸들러)
  const handleListButtonClick = () => {
    // 검색 결과 초기화 및 일반 조회로 전환
    setIsSearchMode(false);
    fetchNotices(1);
  };

  const handleTitleClick = (noticeNo) => {
    // url path 와 ${변수명} 를 같이 사용시에는 반드시 빽틱(``)을 표시해야 함 (작은따옴표 아님 : 주의)
    navigate(`/noticed/${noticeNo}`);   //상세 페이지로 이동 처리 지정   
    //라우터로 등록함 
  };

  const handleWriteClick = () => {
    navigate('/noticew');  //글쓰기 페이지로 이동 처리 지정, 라우터로 등록해야 함
  };

  if (loading) {
    return <div className={styles.loading}>로딩 중...</div>; // 로딩 표시
  }

  if (error) {
    return <div className={styles.error}>{error}</div>; // 에러 메시지 표시
  }

  return (
    <div className={styles.noticeContainer}>
      <h1 className={styles.title}>공지사항</h1>
      {/* 글쓰기 버튼 : role 이 'ADMIN' 일 때만 보여지게 함 */}
      {role === 'ADMIN' && (
          <button onClick={handleWriteClick}>글쓰기</button>
        )
      }
      
      <button onClick={handleListButtonClick}>목록</button>
      <table className={styles.noticeList}>
        <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>첨부파일</th>
            <th>날짜</th>
            <th>조회수</th>
        </tr>
        {notices.map((notice) => (
          <tr key={notice.noticeNo} className={styles.noticeItem}>
            <td className={styles.noticeNo}>{notice.noticeNo}</td>
            <td className={styles.noticeTitle}>
                <span 
                    style={{color: 'blue', cursor: 'pointer', textDecoration: 'underline'}} 
                    onClick={() => handleTitleClick(notice.noticeNo)}  //클릭시 글번호 전달  
                >            
                    {notice.noticeTitle}
                </span>
            </td>
            <td className={styles.noticeWriter}>{notice.noticeWriter}</td>
            <td className={styles.originalFilePath}>{notice.originalFilePath ? '◎' : ''}</td>
            <td className={styles.noticeDate}>{notice.noticeDate}</td>
            <td className={styles.readCount}>{notice.readCount}</td>
          </tr>
        ))}
      </table>
      <PagingView 
        currentPag={pagingInfo.currentPage || 1}
        maxPage={pagingInfo.maxPage || 1}
        startPage={pagingInfo.startPage || 1}
        endPage={pagingInfo.endPage || 1}
        onPageChange={(page) => handlePageChange(page)}
      />
    </div>
  );
}

export default NoticeList;