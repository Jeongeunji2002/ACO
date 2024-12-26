import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/common/Header';
import NoticeList from './pages/notice/NoticeList';  //검색 결과 전달을 위해 개별적으로 임포트함
import BoardList from './pages/board/BoardList';  //검색 결과 전달을 위해 개별적으로 임포트함
import AppRouter from './routers/router'; // 라우터 설정 임포트

function App() {
  const [boardResults, setBoardResults] = useState(null);  //board 검색 결과 상태 관리
  const [noticeResults, setNoticeResults] = useState(null);  //notice 검색 결과 상태 관리

  const resetSearchInput = () => {
    setBoardResults(null);
    setNoticeResults(null);
  };

  return (
    <Router>
      {/* Header 에 검색 결과 업데이트 처리 추가 */}
      <Header 
        updateBoardResults={setBoardResults}
        updateNoticeResults={setNoticeResults}
        resetSearchInput={resetSearchInput}
         />    
      <Routes>
        {/* 검색 결과가 필요한 NoticeList 라우터만 분리 작성, searchResults 를 전달함 */} 
        <Route path="/notice" element={<NoticeList searchResults={noticeResults} />} />, 
        <Route path="/board" element={<BoardList searchResults={boardResults} />} />,
        {/* <AppRouter /> 라우터 설정 */}
        {/* 나머지 경로는 AppRouter 로 처리함 */}
        <Route path="/*" element={<AppRouter />} />
      </Routes>
    </Router>
  );
}

export default App;