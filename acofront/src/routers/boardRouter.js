// src/routers/boardRouter.js
import React from 'react';
import { Route } from 'react-router-dom';

import BoardDetail from '../pages/board/BoardDetail';
import BoardWrite from '../pages/board/BoardWrite';
import BoardUpdate from '../pages/board/BoardUpdate';

const boardRouter = [    
    <Route path="/board/detail/:no" element={<BoardDetail />} />,
    <Route path="/board/update/:no" element={<BoardUpdate />} />,
    <Route path="/board/write" element={<BoardWrite />} />,
];

export default boardRouter;