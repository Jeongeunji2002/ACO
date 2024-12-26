// src/pages/member/Login.js
import React, { useState, useContext } from 'react';
import apiClient from '../../utils/axios';
import { AuthContext } from '../../AuthProvider';
import styles from './Login.module.css';


function Login({ onLoginSuccess }) {  //로그인 성공시 모달창 닫히도록 처리하는 콜백 추가
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const { login } = useContext(AuthContext);  // AuthProvider 에서 login 함수 사용 선언함

  const handleLogin = async (e) => {
    e.preventDefault();  // submit 이벤트 취소함 : axios 가 작동될것이므로, 전송보내지 않도록 함
    
    try {
      const response = await apiClient.post('/login', {
        userId: username,
        userPwd:  password,
      });

      // 해더에서 Authorization 정보 추출
      const authorizationHeader = response.headers['authorization'];
      if (authorizationHeader && authorizationHeader.startsWith('Bearer ')){
        const accessToken = authorizationHeader.substring('Bearer '.length);  //'Bearer ' 이후의 토큰 추출        
      
        //alert('응답값 : ' + response.data);
        //response data 추출 <= 서버측에서 Map 에 저장한 것임
        const { refreshToken } = response.data;
        console.log(refreshToken + '\n' + accessToken);

        // AuthProvider 의 login 함수 호출
        login({ accessToken, refreshToken });
      }     
     
      //alert('로그인 성공!');
      //로그인 성공시 부모 컴포넌트 알림
      if(onLoginSuccess) onLoginSuccess();
    } catch (error) {
      console.error('Login failed : ', error.response?.data || error.message);
      alert('로그인 실패 : 다시 시도하십시오.');
    }
  };

  return (
    <div className={styles.container}>
      <h2>로그인 페이지</h2>
      <form className={styles.form} onSubmit={handleLogin}>
        <div className={styles.inputGroup}>
          <label htmlFor="username">아이디:</label>
          <input type="text" id="username" placeholder="아이디를 입력하세요"
            value={username}
            onChange={(e) => setUsername(e.target.value)} 
          />
        </div>
        <div className={styles.inputGroup}>
          <label htmlFor="password">비밀번호:</label>
          <input type="password" id="password" placeholder="비밀번호를 입력하세요"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <button type="submit" className={styles.button}>로그인</button>
      </form>
    </div>
  );
}

export default Login;
