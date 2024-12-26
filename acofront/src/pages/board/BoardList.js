// src/pages/board/BoardList.js
import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';  //page 에서 page 바꾸기할 때 사용
import apiClient from '../../utils/axios';
import styles from './BoardList.module.css';  //게시글 목록 출력 페이지에만 적용할 스타일시트 파일
import PagingView from '../../components/common/PagingView';  //PagingView 컴포넌트 임포트
import { AuthContext } from '../../AuthProvider';

// Header.js 에서 받은 list 와 paging 정보르 받아서 랜더링하도록 코드 수정함
function BoardList({ searchResults }){
  const [boards, setBoards] = useState([]); // 게시글 데이터를 저장할 상태
  const [pagingInfo, setPagingInfo] = useState({
    currentPage: 1,
    maxPage: 1,
    startPage: 1,
    endPage: 1,
  });

  //현재 동작 상태 관리 (list or search)
  const [isSearchMode, setIsSearchMode] = useState(false);
  const [loading, setLoading] = useState(true); // 로딩 상태 관리  
  const [error, setError] = useState(null); // 에러 상태 관리  

  const { isLoggedIn } = useContext(AuthContext);  // AuthProvider 에서 가져오기

  const navigate = useNavigate();  //페이지 이동을 위한 navigate 함수 선언함  

  // 서버에서 게시글 목록 (기본 1page) 데이터를 가져오는 함수
  const fetchBoards = async (page) => {
    try {
      setLoading(true); // 로딩 상태 시작
      const response = await apiClient.get(`/board`, {
        params: { page },
      }); // Spring Boot 서버 URL
      setBoards(response.data.list); // 응답 데이터를 상태로 설정  //boards = response.data.list; 과 같음
      setPagingInfo(response.data.paging);  //서버에서 제공하는 페이징 정보
      console.log(response.data.paging);
      setIsSearchMode(false);  // 일반 목록 조회 모드 지정
    } catch (err) {
      setError('게시글 목록을 불러오는 데 실패했습니다.'); // 에러 메시지 설정
	//error = '게시글 목록을 불러오는 데 실패했습니다.'; 과 같음
    } finally {
      setLoading(false); // 로딩 상태 종료, loading = false; 과 같음
    }
  };

  //검색 결과가 변경되면 상태 업데이트 코드 추가함 
  // 컴포넌트가 처음 렌더링될 때 fetchNotices 호출
  // window.onload 될때 (jquery.document.ready 과 같음)
  useEffect(() => {
    if(searchResults){
      //검색 결과가 전달되면 검색 모드로 전환 처리함      
      setBoards(searchResults.list || []);
      setPagingInfo(searchResults.paging || {});
      setIsSearchMode(true);   //검색 모드로 설정
      setLoading(false);
    }else{
      // 초기 로드 또는 일반 조회
      fetchBoards(1);
    }
  }, [searchResults]);

  //페이지 변경 핸들러 : 클릭한 page 의 목록을 요청 처리함 (일반 목록 페이지 요청 또는 검색 목록 페이지 요청)
  const handlePageChange = async (page) => {
    try{
      setLoading(true);
      if(isSearchMode){
        // 검색 목록 페이지 요청
        const response = await apiClient.get(`/board/search/title`, {
          params: { action: searchResults.action, keyword: searchResults.keyword, page }
        })
        setBoards(response.data.list || []);
        setPagingInfo(response.data.paging || {});
      } else {
        fetchBoards(page);  //일반 목록 페이지 요청
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
    fetchBoards(1);
  };

  //제목 클릭시 상세보기 이동
  const handleTitleClick = (boardNum) => {
    // url path 와 ${변수명} 를 같이 사용시에는 반드시 빽틱(``)을 표시해야 함 (작은따옴표 아님 : 주의)
    navigate(`/board/detail/${boardNum}`);   //상세 페이지로 이동 처리 지정   
    //라우터로 등록함 
  };

  //글쓰기 버튼 클릭시 글쓰기 페이지로 이동동
  const handleWriteClick = () => {
    navigate('/board/write');  //글쓰기 페이지로 이동 처리 지정, 라우터로 등록해야 함
  };

  if (loading) {
    return <div className={styles.loading}>로딩 중...</div>; // 로딩 표시
  }

  if (error) {
    return <div className={styles.error}>{error}</div>; // 에러 메시지 표시
  }

  return (
    <div className={styles.boardContainer}>
      <h1 className={styles.title}>게시판</h1>
      {/* 글쓰기 버튼 : 로그인 상태일 때만 표시 */}
      {isLoggedIn && (<button onClick={handleWriteClick}>글쓰기</button> )}
      <button onClick={handleListButtonClick}>목록</button>
      <table className={styles.boardList}>
        <thead>
            <tr>
                <th>번호</th>
                <th>제목</th>
                <th>작성자</th>
                <th>첨부파일</th>
                <th>날짜</th>
                <th>조회수</th>
            </tr>
        </thead>
        <tbody>
        {boards.map((board) => (
          <tr key={board.boardNum}>
            <td>{board.boardNum}</td>
            <td>
              {/* 로그인 상태일 때 링크 동작 */}
              {isLoggedIn ?
                (<span 
                    style={{color: 'blue', cursor: 'pointer', textDecoration: 'underline'}} 
                    onClick={() => handleTitleClick(board.boardNum)}  //클릭시 글번호 전달  
                >            
                    {board.boardTitle}
                </span>) : (
                  // 비로그인 상태일 때 링크 비활성화
                  <span style={{ color: 'gray', textDecoration: 'none'}}>{board.boardTitle}</span>
                )
              }
            </td>
            <td>{board.boardWriter}</td>
            <td>{board.boardOriginalFilename ? '◎' : ''}</td>
            <td>{board.boardDate}</td>
            <td>{board.boardReadCount}</td>
          </tr>
        ))}
        </tbody>
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

export default BoardList;