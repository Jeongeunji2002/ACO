// src/pages/member/MemberList.js
import React, { useState, useEffect, useContext } from "react";
import { AuthContext } from "../../AuthProvider";
import apiClient from "../../utils/axios";
import styles from "./MemberList.module.css";
import PagingView from '../../components/common/PagingView';

const MemberList = () => {
    const { accessToken } = useContext(AuthContext);
    const [members, setMembers] = useState([]);
    const [pagingInfo, setPagingInfo] = useState({
        currentPage: 1,
        maxPage: 1,
        startPage: 1, 
        endPage: 1,
    });

    useEffect(() => {
        //함수실행
        fetchMemberList();
    }, []);

    //useEffect 에서 실행할 함수 작성
    const fetchMemberList = async (page = 1) => {
        try {
            const response = await apiClient.get(`/member?page=${page}`, {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
            });
            setMembers(response.data.list);
            setPagingInfo(response.data.paging);
        } catch (error) {
            console.error('회원 목록 조회 실패 : ', error);
            alert('관리자용 회원 목록 조회 실패했습니다. 사이트 관리자에게 문의하세요.');
        }
    };  //fetchMemberList

    const handleLoginChange = async (userId, loginOk) => {
        try {
            console.log(userId + " : " + loginOk);
            const updateLoginOk = loginOk === 'Y' ? 'N' : 'Y';
            console.log(userId + " : " + updateLoginOk);
            await apiClient.put(`/member/loginok/${userId}/${updateLoginOk}`, {}, {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
            });
            fetchMemberList();
        } catch (error) {
            console.error('로그인 허용/제한 변경 처리 실패 : ', error);
            alert('로그인 허용/제한 변경 처리 실패! 사이트 관리자에게 문의하세요.');
        }
    };  //handleLoginChange

    //페이지 변경 핸들러 : 클릭한 page 의 목록을 요청 처리함 
    const handlePageChange = async (page) => {
        try{        
            fetchMemberList(page);  //일반 목록 페이지 요청
        } catch (error){
            console.error('회원 목록 조회 실패 : ', error);
            alert('관리자용 회원 목록 조회 실패했습니다. 사이트 관리자에게 문의하세요.');
        }
    };

    const renderMemberRow = (member) => (
        <tr key={member.userId}>
            <td>{member.userId}</td>
            <td>{member.userName}</td>
            <td>{member.gender === 'M' ? '남자' : '여자'}</td>
            <td>{member.age}</td>
            <td>{member.phone}</td>
            <td>{member.email}</td>
            <td>{member.enrollDate}</td>
            <td>{member.lastModified}</td>
            <td>{member.signType}</td>
            <td>
                <label><input type="radio" name={`loginok_${member.userId}`}
                            checked={member.loginOk === 'Y'}
                            onChange={() => handleLoginChange(member.userId, member.loginOk)} /> 허용</label>
                <label style={{ marginLeft: '10px' }}>
                    <input type="radio" name={`loginok_${member.userId}`} 
                            checked={member.loginOk === 'N'}
                            onChange={() => handleLoginChange(member.userId, member.loginOk)} /> 제한</label>
            </td>
        </tr>
    );

    return (
        <div className={styles.memberlist}>
            <h1 style={{ textAlign: 'center' }}>회원 목록</h1>
            <table border="1" align="center" cellSpacing="0" cellPadding="0">
                <thead>
                    <th>아이디</th>
                    <th>이름</th>
                    <th>성별</th>
                    <th>나이</th>
                    <th>전화번호</th>
                    <th>이메일</th>
                    <th>가입날짜</th>
                    <th>마지막 수정날짜</th>
                    <th>가입방식</th>
                    <th>로그인 제한여부</th>
                </thead>
                <tbody>
                    {members.length > 0 ? members.map(renderMemberRow) : (
                        <tr>
                            <td colSpan="10" align="center">가입된 회원이 존재하지 않습니다.</td>
                        </tr>
                    )}
                </tbody>
            </table>
            <PagingView 
                currentPag={pagingInfo.currentPage || 1}
                maxPage={pagingInfo.maxPage || 1}
                startPage={pagingInfo.startPage || 1}
                endPage={pagingInfo.endPage || 1}
                onPageChange={(page) => handlePageChange(page)}
            />
        </div>
    );
};  //MemberList

export default MemberList;
