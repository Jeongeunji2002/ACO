import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/common/Header';  //검색 결과 전달을 위해 개별적으로 임포트함 

function App() {
  const [boardResults, setBoardResults] = useState(null);  //board 검색 결과 상태 관리
  const [chatResults, setChatResults] = useState(null);  //notice 검색 결과 상태 관리

  const resetSearchInput = () => {
    setBoardResults(null);
    setChatResults(null);
    
  };

  return (
    <Router>
      {/* Header 에 검색 결과 업데이트 처리 추가 */}
      <Header 
        updateBoardResults={setBoardResults}
        updateChatResults={setChatResults}
        resetSearchInput={resetSearchInput}
         />
    </Router>
  );
}

export default App;