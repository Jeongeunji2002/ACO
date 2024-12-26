// src/routers/noticeRouter.js
import React from 'react';
import { Route } from 'react-router-dom';

import NoticeDetail from '../pages/notice/NoticeDetail';
import NoticeWrite from '../pages/notice/NoticeWrite';
import NoticeUpdate from '../pages/notice/NoticeUpdate';

const noticeRouter = [    
    <Route path="/noticed/:no" element={<NoticeDetail />} />,
    <Route path="/noticew" element={<NoticeWrite />} />,
    <Route path="/noticeu/:no" element={<NoticeUpdate />} />,
];

export default noticeRouter;