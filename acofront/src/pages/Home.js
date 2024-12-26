// src/pages/Home.js
import React, { useEffect, useState, useContext } from 'react';
import { Link } from 'react-router-dom';
import apiClient from '../utils/axios';
import styles from './Home.module.css';
import { AuthContext } from '../AuthProvider';

function Home() {
  // 상태 관리 변수 선언
  const [ntop3, setNtop3] = useState([]);
  const [btop3, setBtop3] = useState([]);
  const [loading, setLoading] = useState(true);

  const { isLoggedIn } = useContext(AuthContext);  // 로그인 상태 확인, AuthProvider 에서 가져오기

  // 페이지가 로딩될때 (랜더링될 때) 자동 작동되는 훅(hook)
  // 페이지 새로고침 처리도 담당함
  // window.onload 와 같음
  useEffect(() => {
    const fetchData = async () => {
      try{
        const ntop3Response = await apiClient.get('/notice/ntop3');
        setNtop3(ntop3Response.data);
        console.log(ntop3);

        const btop3Response = await apiClient.get('/board/btop3');
        setBtop3(btop3Response.data);
        console.log(btop3);

        setLoading(false);
      }catch(error){
        console.error('top3 fetch error : ', error);
        setLoading(false);
      }
    };  // fetchData 함수 작성

    //fetchData 함수 실행
    fetchData();
  }, []);

  if (loading){
    return <div className={styles.container}>Loading...</div>
  }

  return (
    <div className={styles.container}>
      <div className={styles.content}>
        {/* Left Section : Notice Top 3 */}
        <section className={styles.secition}>
          <h2>신규 공지사항 3</h2>
          <table className={styles.table}>
            <thead>
              <tr>
                <th>번호</th>
                <th>제목</th>
                <th>작성자</th>
                <th>날짜</th>
              </tr>
            </thead>
            <tbody>
              {ntop3.map((item) => (
                <tr key={item.noticeNo}>
                  <td>{item.noticeNo}</td>
                  <td><Link to={`/noticed/${item.noticeNo}`} className={styles.link}>{item.noticeTitle}</Link></td>
                  <td>{item.noticeWriter}</td>
                  <td>{item.noticeDate}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        {/* Right Section : Board Top 3 */}
        <section className={styles.secition}>
          <h2>인기 게시글 3</h2>
          <table className={styles.table}>
            <thead>
              <tr>
                <th>번호</th>
                <th>제목</th> 
                <th>작성자</th>               
                <th>조회수</th>
              </tr>
            </thead>
            <tbody>
              {btop3.map((item) => (
                <tr key={item.boardNum}>
                  <td>{item.boardNum}</td>
                  <td>
                    {isLoggedIn ? (
                      <Link to={`/board/detail/${item.boardNum}`} className={styles.link}>{item.boardTitle}</Link>
                    ) : (<span className={styles.text}>{item.boardTitle}</span>)
                    }
                  </td>                  
                  <td>{item.boardWriter}</td>
                  <td>{item.boardReadCount}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      </div>
    </div>
  );
}

export default Home;