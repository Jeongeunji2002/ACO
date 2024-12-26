// src/components/common/Modal.js
// 리액트가 제공하는 'Potals' 이용한 모달 컴포넌트
// 'Potals' 는 DOM 의 특정 노드로 React 컴포넌트 트리를 랜더링할 수 있도록 해 주는 기능임

import React from 'react';
import ReactDOM from 'react-dom';
import styles from './Modal.module.css';

const Modal = ({ children, onClose }) => {
  return ReactDOM.createPortal(
    <div className={styles.modalOverlay}>
      <div className={styles.modalContent}>
        <button onClick={onClose} className={styles.closeButton}>X</button>
        {children}
      </div>
    </div>,
    document.getElementById('portal-root') // Portals를 사용할 DOM 노드
    // public/index.html 에 노드 추가 지정
  );
};

export default Modal;