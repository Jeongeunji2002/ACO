// src/pages/reply/ReplyWrite.js
import React, { useState, useContext } from "react";
import apiClient from "../../utils/axios";
import { AuthContext } from "../../AuthProvider";
import styles from './ReplyWrite.module.css';

const ReplyWrite = ({ boardNum, replyNum, onReplyAdded }) => {
    const { userid, accessToken } = useContext(AuthContext);  // AuthProvider 에서 가져오기
    const [replyTitle, setReplyTitle] = useState('');
    const [replyContent, setReplyContent] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();   //발생한 submit 이벤트 취소시킴

        try{
            const formData = new FormData();
            formData.append('boardRef', boardNum);  //항상 전달받음, 참조원글번호
            
            if (replyNum) {  // 대댓글 등록일때만 전달받음
                formData.append('replyReplyRef', replyNum);  //참조댓글번호
                formData.append('replyLev', 2);  // 댓글 레벨 변경
            }else {
                formData.append('replyLev', 1);
            }
            formData.append('replyWriter', userid);
            formData.append('replyTitle', replyTitle);
            formData.append('replyContent', replyContent);

            await apiClient.post('/reply', formData, {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                    'Content-Type': 'multipart/form-data',
                }
            });

            alert('댓글 또는 대댓글이 등록되었습니다.');
            setReplyTitle('');
            setReplyContent('');
            //모달 닫기
            if(onReplyAdded) onReplyAdded();
        }catch(error){
            console.error('댓글 등록 실패 : ', error);
            alert('댓글 등록에 실패했습니다.');
        }
    };

    return (
        <form className={styles.replyForm} onSubmit={handleSubmit}>
            <div>
                <label>작성자 : </label>
                <input type="text" value={userid} readOnly className={styles.readOnlyInput} />
            </div>
            <div>
                <label>제목 : </label>
                <input type="text" 
                    value={replyTitle} 
                    onChange={(e) => setReplyTitle(e.target.value)} 
                    placeholder="제목을 입력하세요."
                    required />
            </div>
            <div>
                <label>내용 : </label>
                <textarea value={replyContent} 
                    onChange={(e) => setReplyContent(e.target.value)}
                    rows="4"
                    placeholder="내용을 입력하세요."
                    required></textarea>
            </div>
            <button type="submit" className={styles.submitButton}>등록</button>
        </form>
    );
};

export default ReplyWrite;