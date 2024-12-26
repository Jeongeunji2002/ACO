// src/pages/notice/NoticeWrite.js
import React, { useEffect, useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../../utils/axios';
import styles from './NoticeWrite.module.css';
import { AuthContext } from '../../AuthProvider';

function NoticeWrite() {
    const { userid, accessToken } = useContext(AuthContext); // AuthProvider 에서 가져오기

    //상태 변수 지정
    const [formData, setFormData] = useState({
        noticeTitle: '',
        noticeWriter: '', //초기 상태      
        importance: false,
        impEndDate:  new Date().toISOString().split('T')[0], // 오늘 날짜 기본값 (yyyy-MM-dd)
        noticeContent: '',
    });
    //첨부할 파일은 formData 와 별개로 지정함
    const [file, setFile] = useState(null);
    // 글등록 성공시 'NoticeList' 페이지로 이동 처리할 것이므로
    const navigate = useNavigate();

    //페이지가 로딩될 때 (window.onload or jquery.document.ready 와 같음)
    useEffect(() => {   
        setFormData((prevFormData) => ({
            ...prevFormData,
            noticeWriter: userid, // AuthProvider 에서 가져온 userid
        }));
        
    }, [userid]);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData((prevFormData) => ({
            ...prevFormData,
            [name]: type === 'checkbox' ? checked : value,

        }));
    };

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);  // input 에서 선택한 파일명을 file 변수에 적용함
    };

    const handleSubmit = async (e) => {
        e.preventDefault(); // 기본 폼 제출 방지 (submit 이벤트 취소함)

        const data = new FormData();
        data.append('noticeTitle', formData.noticeTitle);
        data.append('noticeWriter', formData.noticeWriter);
        data.append('importance', formData.importance);
        data.append('impEndDate', formData.impEndDate);
        data.append('noticeContent', formData.noticeContent);
        if(file){
            data.append('ofile', file); // 첨부파일 추가
        }

        try {
            await apiClient.post('/notice', data, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    Authorization: `Bearer ${accessToken}`, // accessToken 추가
                }
            });

            alert('새 공지글 등록 성공');
            // 공지글 등록이 성공되면 공지 목록 페이지로 이동
            navigate('/notice');
        } catch (error) {
            console.error('공지글 등록 실패', error);
            alert('새 공지글 등록 실패');
        }
    };

    return (
        <div className={styles.container}>
        <h1 className={styles.header}>새 공지글 등록 페이지</h1>
        <form 
            onSubmit={handleSubmit}
            enctype="multipart/form-data"
            className={styles.form}>
            <table id="outer" align="center" width="700" cellspacing="5" cellpadding="5">   
                <tbody>
                <tr><th width="120">제 목</th>
                    <td>
                        <input 
                            type="text" 
                            name="noticeTitle" 
                            size="50"
                            value={formData.noticeTitle}
                            onChange={handleChange}
                            required />         
                    </td></tr>
                <tr><th width="120">작성자</th>
                    <td>
                        <input 
                            type="text" 
                            name="noticeWriter" 
                            value={formData.noticeWriter}
                            readonly />         
                    </td></tr>
                <tr><th width="120">중요도</th>
                    <td>
                        <input 
                            type="checkbox" 
                            name="importance" 
                            value="Y"
                            checked={formData.importance}
                            onChange={handleChange}
                            />{' '} 중요         
                    </td></tr>
                <tr><th width="120">중요도 지정 종료 날짜</th>
                    <td>
                        <input 
                            type="date" 
                            name="impEndDate" 
                            id="impEndDate"
                            value={formData.impEndDate}
                            onChange={handleChange}
                            />
                    </td></tr>
                <tr><th>첨부파일</th>
                    <td>
                        <input type="file" name="ofile" onChange={handleFileChange} />                         
                    </td>
                </tr>
                <tr><th>내 용</th>
                    <td><textarea 
                            rows="5" 
                            cols="50" 
                            name="noticeContent"
                            value={formData.noticeContent}
                            onChange={handleChange}
                            required
                            ></textarea>
                    </td></tr>
                
                <tr><th colspan="2">
                    <input type="submit" value="등록하기" /> &nbsp; 
                    <input type="reset" value="작성취소" 
                        onClick={() => setFormData({ ...formData, noticeContent: ''})} />{' '} &nbsp;
                    <input 
                        type="button" 
                        value="목록" 
                        onClick={() => {
                            navigate(-1);   //자바스크립트의 history.go(-1); 과 같음
                        }} />
                </th></tr>
                </tbody>
            </table>
            </form>
        </div>
    );
}

export default NoticeWrite;
