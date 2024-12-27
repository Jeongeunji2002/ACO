// src/routers/noticeRouter.js
import React from 'react';
import { Route } from 'react-router-dom';

import NoticeDetail from '../pages/aichatting/NoticeDetail';

const chatRouter = [    
    <Route path="/noticed/:no" element={<NoticeDetail />} />,
];

export default chatRouter;