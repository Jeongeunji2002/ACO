// src/routers/router.js
import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Home from '../pages/Home';
//통합하기 위해 별도로 작성된 라우터 파일들을 임포트함
import memberRouter from './memberRouter';
import chatRouter from './chatRouter';

const AppRouter = () => {
  return (
    <Routes>
      {/* 통합할 라우터 파일명 */}
      {memberRouter}
      {chatRouter}
      <Route path="/" element={<Home />} />
    </Routes>
  );
};

export default AppRouter;