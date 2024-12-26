import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { AuthProvider } from './AuthProvider'; // AuthProvider 가져오기
import { BrowserRouter } from 'react-router-dom';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    {/* <React.StrictMode>는 React의 개발 모드에서만 동작하는 컴포넌트입니다. 
        프로덕션 빌드에서는 실행되지 않으며, 개발 중에만 코드의 잠재적인 문제를 감지해 경고를 표시합니다.
        개발 중에 React.StrictMode로 감싸진 컴포넌트는 두 번 렌더링됩니다. 
        이를 통해 의심스러운 사이드 이펙트를 감지합니다.
        (프로덕션에서는 두 번 렌더링되지 않습니다.)
        - componentWillMount, componentWillReceiveProps, componentWillUpdate와 같은 오래된 생명주기 메서드 사용 시 경고를 표시합니다.
        - 의심스러운 사이드 이펙트 감지
          컴포넌트의 렌더링, 업데이트 과정에서 발생하는 의심스러운 사이드 이펙트를 감지합니다. 
          예를 들어:
            useEffect의 잘못된 정리 함수
            동기화되지 않은 상태 업데이트
            불필요한 렌더링
    */}
    <AuthProvider>
      {/* <BrowserRouter> 검색 결과 처리 코드 수정으로 삭제해야 함 */}
        <App />
      {/* </BrowserRouter> */}
    </AuthProvider>
    </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
