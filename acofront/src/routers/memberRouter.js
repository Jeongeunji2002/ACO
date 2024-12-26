// src/routers/memberRouter.js
import React from 'react';
import { Route } from 'react-router-dom';
import Login from '../pages/member/Login';
import Signup from '../pages/member/Signup';
import MemberInfo from '../pages/member/MemberInfo';
import MemberList from '../pages/member/MemberList';

const memberRouter = [
    <Route path="/login" element={<Login />} />,
    <Route path="/signup" element={<Signup />} />,
    <Route path="/profile" element={<MemberInfo />} />,
    <Route path="/mlist" element={<MemberList />} />,
];

export default memberRouter;