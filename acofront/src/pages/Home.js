// src/pages/Home.js
import React, { useEffect, useState, useContext } from 'react';
import { Link } from 'react-router-dom';
import apiClient from '../utils/axios';
import styles from './Home.module.css';
import { AuthContext } from '../AuthProvider';

function Home() {
  const [ntop3, setNtop3] = useState([]);
  const [btop3, setBtop3] = useState([]);
  const [loading, setLoading] = useState(true);
  const { isLoggedIn } = useContext(AuthContext);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [ntop3Response, btop3Response] = await Promise.all([
          apiClient.get('/notice/ntop3'),
          apiClient.get('/board/btop3')
        ]);
        
        setNtop3(ntop3Response.data);
        setBtop3(btop3Response.data);
      } catch (error) {
        console.error('Error fetching top 3 data: ', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return <div className={styles.container}>Loading...</div>;
  }

  const renderTableRows = (data, isBoard = false) => {
    return data.map(item => (
      <tr key={isBoard ? item.boardNum : item.noticeNo}>
        <td>{isBoard ? item.boardNum : item.noticeNo}</td>
        <td>
          {isBoard && !isLoggedIn ? (
            <span className={styles.text}>{item.boardTitle}</span>
          ) : (
            <Link to={isBoard ? `/board/detail/${item.boardNum}` : `/noticed/${item.noticeNo}`} className={styles.link}>
              {isBoard ? item.boardTitle : item.noticeTitle}
            </Link>
          )}
        </td>
        <td>{isBoard ? item.boardWriter : item.noticeWriter}</td>
        <td>{isBoard ? item.boardReadCount : item.noticeDate}</td>
      </tr>
    ));
  };

  return (
    <div className={styles.container}>
      <div className={styles.content}>
        {/* Left Section: Notice Top 3 */}
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
              {renderTableRows(ntop3)}
            </tbody>
          </table>
        </section>

        {/* Right Section: Board Top 3 */}
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
              {renderTableRows(btop3, true)}
            </tbody>
          </table>
        </section>
      </div>
    </div>
  );
}

export default Home;
