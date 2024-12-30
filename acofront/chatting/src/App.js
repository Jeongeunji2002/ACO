// src/App.js
import React, { useState } from 'react';
import Header from './Header';
import Chat from './Chat';
import ChatList from './ChatList'; // ChatList 컴포넌트 임포트
import './App.css';

function App() {
    const [chats, setChats] = useState(['채팅 1', '채팅 2']); // 기본 채팅 리스트
    const [selectedChat, setSelectedChat] = useState(null);
    const [isChatListVisible, setChatListVisible] = useState(true); // 채팅 리스트 가시성 상태

    const handleChatSelect = (chat) => {
        setSelectedChat(chat);
        // 추가적인 로직을 여기에 추가할 수 있습니다.
    };

    const handleNewChat = () => {
        const newChatName = prompt('새로운 채팅 이름을 입력하세요:');
        if (newChatName) {
            setChats([...chats, newChatName]);
        }
    };

    const toggleChatList = () => {
        setChatListVisible(!isChatListVisible); // 가시성 토글
    };

    return (
        <div className="App">
            <Header />
            <div className="main-container">
                {isChatListVisible && (
                    <ChatList 
                        chats={chats} 
                        onChatSelect={handleChatSelect} 
                        onNewChat={handleNewChat} 
                    />
                )}
                <div className="chat-area">
                    <button className={`toggle-button ${isChatListVisible ? 'open' : 'closed'}`} onClick={toggleChatList}>
                        {isChatListVisible ? '<' : '>'}
                    </button>
                    <Chat selectedChat={selectedChat} />
                </div>
            </div>
        </div>
    );
}

export default App;
