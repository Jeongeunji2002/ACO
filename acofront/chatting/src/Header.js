// src/Header.js
import React from 'react';
import './Header.css';
import logo from './logo/logo.png'; // 로고 이미지 임포트

const Header = () => {
    return (
        <header className="header">
            <div className="header-logo">
                <img src={logo} alt="로고" className="logo" />
                <span className="header-title">NEXT AI</span>
            </div>
            <nav className="header-nav">
                <a href="#">홈</a>
                <a href="#">정보</a>
                <a href="#">설정</a>
            </nav>
        </header>
    );
};

export default Header;
