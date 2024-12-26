import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import styles from './SideMenu.module.css'; 
import { AuthContext } from '../../AuthProvider';

function SideMenu() {
  const { isLoggedIn, role, logout } = useContext(AuthContext);  //AuthProvider 에서 가져오기

  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <aside className={styles.sideMenu}>
      <ul className={styles.menuList}>
        <li>
          {isLoggedIn ? (
            <Link to="/profile">내 프로필</Link>
            ) : (
              <span className={styles.disabled}>내 프로필</span>
            )}          
        </li>
        {isLoggedIn && (<li><button onClick={handleLogout}>로그아웃</button></li>)}  
        <li>
          {isLoggedIn && role === 'ADMIN' ? (
              <Link to="/mlist">회원관리</Link>
              ) : (
                <span className={styles.disabled}>회원관리</span>
          )} 
        </li>      
        <li><Link to="/settings">설정</Link></li>
        <li><Link to="/help">도움말</Link></li>        
      </ul>
    </aside>
  );
}

export default SideMenu;
