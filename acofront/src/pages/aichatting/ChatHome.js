// src/pages/aichatting/Chathome.js  => 공지글 목록 출력 페이지
import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';  //page 에서 page 바꾸기할 때 사용
import apiClient from '../../utils/axios';
import styles from './ChatHome.module.css';  //공지글 목록 출력 페이지에만 적용할 스타일시트 파일
import PagingView from '../../components/common/PagingView';  //PagingView 컴포넌트 임포트
import { AuthContext } from '../../AuthProvider';  // AuthProvider 에서 가져오기 위해 임포트

// Header.js 에서 받은 list 와 paging 정보르 받아서 랜더링하도록 코드 수정함
const ChatHome = () => {
    const chatRooms = [
        { id: 1, title: '정진지', description: '잠이 안되는 이유에 대해서 모르겠어요.' },
        { id: 2, title: '김상태, 이현화', description: '이제 겨우 나가는 것만 마드모아젤로, 모든 걸 다 할 수는 없는데' },
        { id: 3, title: '오정민', description: '이메일은 좀 더 여유 있게 해야겠지요.' },
        { id: 4, title: '이현준', description: '잠이 안되는 이유는 여전히 모르겠어요. 저도 힘들어요.' },
        { id: 5, title: '박상진', description: '박사님의 시원한 해답과 함께 하는 방입니다.' },
        { id: 6, title: '박재훈', description: '잠이 안 오는 이유에 대해 이야기해요.' },
        { id: 7, title: '박지훈', description: '이해할 수 없는 이야기를 나누는 방이에요.' },
        { id: 8, title: '정진지, 오정민', description: '잠이 많지만 이해해보려 해요.' },
    ];

    return (
        <div className="chat-home">
            <header>
                <h1>검색란 학문을 입력해 주세요.</h1>
                <input type="text" placeholder="검색..." />
                <button>로그인</button>
            </header>
            <div className="chat-rooms">
                {chatRooms.map(room => (
                    <div key={room.id} className="chat-room">
                        <h2>{room.title}</h2>
                        <p>{room.description}</p>
                        <button>Go somewhere</button>
                    </div>
                ))}
            </div>
            <footer>
                <p>ACO © 2024. Aco Co. All rights reserved.</p>
                <p>공지사항 | Q&A | 오시는 길 | 제작자</p>
            </footer>
        </div>
    );
};

export default ChatHome;