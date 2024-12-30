// src/ChatList.js
import React from 'react';
import './ChatList.css';

const ChatList = ({ chats, onChatSelect, onNewChat }) => {
    return (
        <div className="chat-list">
            <button className="new-chat-button" onClick={onNewChat}>
                새로운 채팅 열기
            </button>
            {chats.map((chat, index) => (
                <div key={index} className="chat-item" onClick={() => onChatSelect(chat)}>
                    {chat}
                </div>
            ))}
        </div>
    );
};

export default ChatList;
