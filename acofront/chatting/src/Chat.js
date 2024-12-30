// src/Chat.js
import React, { useEffect, useState } from 'react';
import io from 'socket.io-client';
import './Chat.css';

const socket = io('http://localhost:4000'); // 서버 주소

const Chat = ({ selectedChat }) => {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');

    useEffect(() => {
        socket.on('message', (message) => {
            setMessages((prevMessages) => [...prevMessages, message]);
        });

        return () => {
            socket.off('message');
        };
    }, []);

    const sendMessage = () => {
        if (input) {
            socket.emit('message', input);
            setInput('');
        }
    };

    return (
        <div className="chat-container">
            <h2>{selectedChat || '채팅을 선택하세요'}</h2>
            <div className="chat-window">
                {messages.map((msg, index) => (
                    <div key={index} className="chat-message">
                        {msg}
                    </div>
                ))}
            </div>
            <div className="input-container">
                <input 
                    type="text" 
                    value={input} 
                    onChange={(e) => setInput(e.target.value)} 
                    placeholder="메시지를 입력하세요" 
                    className="chat-input"
                />
                <button onClick={sendMessage} className="send-button">전송</button>
            </div>
        </div>
    );
};

export default Chat;
