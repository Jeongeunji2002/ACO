// src/pages/board/BoardWrite.js
import React, { useEffect, useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../../utils/axios';
import { AuthContext } from '../../AuthProvider';
import styles from './BoardWrite.module.css';

const BoardWrite = () => {
    const { userid, accessToken } = useContext(AuthContext); // AuthProvider에서 가져옴

    // 상태 변수 지정
    const [formData, setFormData] = useState({
        boardTitle: '',
        boardWriter: userid, // authInfo의 userid 지정
        boardContent: '',
    });
    const [file, setFile] = useState(null);
    const navigate = useNavigate();

    // 작성자 필드가 userid 으로 동기화되도록 처리
    useEffect(() => {
        setFormData((prevFormData) => ({
            ...prevFormData,
            boardWriter: userid,
        }));
    }, [userid]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prevFormData) => ({
            ...prevFormData,
            [name]: value,
        }));
    };

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const data = new FormData();
        Object.entries(formData).forEach(([key, value]) => data.append(key, value));
        if (file) {
            data.append('ofile', file);
        }

        try {
            await apiClient.post('/board', data, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    Authorization: `Bearer ${accessToken}`, // accessToken 추가
                },
            });

            alert('새 게시글 등록 성공');
            navigate('/board');
        } catch (error) {
            console.error('게시글 등록 실패', error);
            alert('새 게시글 등록 실패');
        }
    };

    return (
        <div className={styles.container}>
            <h1 className={styles.header}>새 게시글 등록 페이지</h1>
            <form onSubmit={handleSubmit} className={styles.form}>
                <table id="outer" align="center" width="700" cellspacing="5" cellpadding="5">
                    <tbody>
                        <tr>
                            <th width="120">제 목</th>
                            <td>
                                <input
                                    type="text"
                                    name="boardTitle"
                                    size="50"
                                    value={formData.boardTitle}
                                    onChange={handleChange}
                                    required
                                />
                            </td>
                        </tr>
                        <tr>
                            <th width="120">작성자</th>
                            <td>
                                <input
                                    type="text"
                                    name="boardWriter"
                                    value={formData.boardWriter}
                                    readOnly
                                />
                            </td>
                        </tr>
                        <tr>
                            <th>첨부파일</th>
                            <td>
                                <input type="file" name="ofile" onChange={handleFileChange} />
                            </td>
                        </tr>
                        <tr>
                            <th>내 용</th>
                            <td>
                                <textarea
                                    rows="5"
                                    cols="50"
                                    name="boardContent"
                                    value={formData.boardContent}
                                    onChange={handleChange}
                                    required
                                ></textarea>
                            </td>
                        </tr>
                        <tr>
                            <th colSpan="2">
                                <input type="submit" value="등록하기" /> &nbsp;
                                <input
                                    type="reset"
                                    value="작성취소"
                                    onClick={() =>
                                        setFormData({ ...formData, boardContent: '' })
                                    }
                                />{' '}
                                &nbsp;
                                <input
                                    type="button"
                                    value="목록"
                                    onClick={() => {
                                        navigate(-1);
                                    }}
                                />
                            </th>
                        </tr>
                    </tbody>
                </table>
            </form>
        </div>
    );
};

export default BoardWrite;
