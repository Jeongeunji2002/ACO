// src/pages/board/BoardDetail.js  => 게시글 상세보기 출력 페이지
import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';  //이전 페이지에서 전달온 값을 받기 위함
import apiClient from '../../utils/axios';
import styles from './BoardDetail.module.css';
import { AuthContext } from '../../AuthProvider';
import Modal from '../../components/common/Modal';
import ReplyWrite from '../reply/ReplyWrite';

const BoardDetail = () => {
    const { no } = useParams();  // URL 에서 no 파라미터를 가져옴(추출함)
    // const history = useHistory();  //이전에 기억된 페이지로 이동하기 위함 (navigate 사용도 가능함)
    // react 6.0 이상에서는 deprecated 됨
    const navigate = useNavigate();  //useNavigate 훅 사용
    const [board, setBoard] = useState(null);  //게시글 데이터 저장할 상태 변수 선언과 초기화함
    const [replies, setReplies] = useState([]);  //댓글 데이터 상태 관리
    const [error, setError] = useState(null);  //에러 메세지 저장용 상태 변수 선언과 초기화

    // 모달 처리용
    const [showModal, setShowModal] = useState(false);
    // 댓글 | 대댓글 등록 타겟 변수
    const [replyTarget, setReplyTarget] = useState(null);

    // 댓글 | 대댓글 수정 상태 관리
    const [editingReply, setEditingReply] = useState(null);  //수정중인 댓글 변호(ID) 저장
    const [editingTitle, setEditingTitle] = useState('');  //수정 중인 댓글 제목 저장용
    const [editingContent, setEditingContent] = useState('');  //수정 중인 댓글 내용 저장용

    const { isLoggedIn, userid, accessToken } = useContext(AuthContext); //AuthProvider 에서 가져오기

    useEffect(() => {
        // 게시글 및 댓글 데이터 요청청
        //서버측에 요청해서 해당 게시글 가져오는 ajax 통신 처리 함수를 작성할 수 있음
        const fetchBoardDetail = async () => {
            console.log('no : ' + no);
            try {
                // url path 와 ${변수명} 를 같이 사용시에는 반드시 빽틱(``)을 표시해야 함 (작은따옴표 아님 : 주의)
                const response = await apiClient.get(`/board/detail/${no}`);
                setBoard(response.data.board);  //서버측에서 받은 데이터 저장 처리
                setReplies(response.data.list);  // 댓글 데이터 저장
                console.log(response.data.board);
                console.log(response.data.list);
            } catch (error) {
                setError('게시글 상세 조회 실패!');
                console.error(error);
            }
        };

        //작성된 함수 실행
        fetchBoardDetail();
    }, [no]);

    // 모달창 열기 함수
    const openModal = ({ boardNum, replyNum = null }) => {
        setReplyTarget({ boardNum, replyNum });
        setShowModal(true);
    };

    // 모달창 닫기 함수
    const closeModal = () => {
        setShowModal(false);
        setReplyTarget(null);
        window.location.reload();  // 페이지 새로고침 추가
    };

    const handleFileDownload = async (originalFileName, renameFileName) => {
        try {
            const response = await apiClient.get('/board/bfdown', {
                params: {
                    ofile: originalFileName,
                    rfile: renameFileName,
                },
                responseType: 'blob',  //파일 다운로드를 위한 설정
            });

            //파일 다운로드 처리
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', originalFileName);
            document.body.appendChild(link);
            link.click();
            link.remove();
        } catch (error) {
            console.error('File download error : ', error);
            alert('파일 다운로드에 실패했습니다.');
        }
    };

    const handleMoveEdit = () => {
        navigate(`/board/update/${no}`);
    };

    const handleDelete = async (renameFilename) => {
        if (window.confirm('정말 삭제하시겠습니까?')) {
            try {
                await apiClient.delete(`/board/${no}`, {
                    params: { rfile: renameFilename },
                    headers: {
                        Authorization: `Bearer ${accessToken}`, // accessToken 추가
                    },
                });
                alert('삭제가 완료되었습니다.');
                //브라우저 히스토리를 이용해서, 목록 출력 페이지로 이동 <= 리액트의 히스토리를 이용한다면
                //history.push('/notice');
                navigate('/board');  //목록 출력 페이지로 이동
            } catch (error) {
                console.error('Delete error : ', error);
                alert('삭제 실패!');
            }
        }
    };

    const handleReplyDelete = async (replyNum) => {
        if(window.confirm('댓글|대댓글을 삭제하시겠습니까?')){
            try{
                await apiClient.delete(`/reply/${replyNum}`, {
                    headers: {
                        Authorization: `Bearer ${accessToken}`, // accessToken 추가
                    },
                });
                setReplies((prevReplies) => prevReplies.filter((reply) => reply.replyNum !== replyNum));
            }catch(error){
                alert('댓글|대댓글 삭제에 실패했습니다.');
            }
        }
    };

    // 댓글 | 대댓글 수정 버튼 클릭시, 제목과 내용이 input 으로 변경 처리하는 핸들러
    const handleReplyEdit = (replyNum, replyTitle, replyContent) => {
        setEditingReply(replyNum);
        setEditingTitle(replyTitle);
        setEditingContent(replyContent);
    };

    // 댓글 | 대댓글 수정하고 저장 버튼 클릭시 작동할 핸들러
    const handleSaveReplyEdit = async (replyNum) => {
        try {
            await apiClient.put(`/reply/${replyNum}`, {
                replyNum: replyNum,
                replyTitle: editingTitle,
                replyContent: editingContent
            }, {
                headers: { Authorization: `Bearer ${accessToken}`}
            });

            setReplies((prevReplies) => prevReplies.map((reply) => reply.replyNum === replyNum 
                                            ? { ...reply, replyTitle: editingTitle, replyContent: editingContent} 
                                            : reply));
            setEditingReply(null);
            alert('댓글이 수정되었습니다.');
        } catch (error) {
            alert('댓글 | 대댓글 수정 실패했습니다.');
        }
    };

    

    if (!board) {
        return <div className={styles.loading}>로딩 중...</div>; // 로딩 표시
    }
    
    if (error) {
        return <div className={styles.error}>{error}</div>; // 에러 메시지 표시
    }

    return (
        <div>
            <h2> { no }번 게시글 상세보기</h2>
            <table border="1">
                <tbody>
                    <tr>
                        <th>번호</th>
                        <td>{board.boardNum}</td>
                    </tr>
                    <tr>
                        <th>제목</th>
                        <td>{board.boardTitle}</td>
                    </tr>
                    <tr>
                        <th>작성자</th>
                        <td>{board.boardWriter}</td>
                    </tr>
                    <tr>
                        <th>첨부파일</th>
                        <td>{board.boardOriginalFilename ? (
                            <button
                                onClick={() => 
                                            handleFileDownload(board.boardOriginalFilename, board.boardRenameFilename)
                                        }>{board.boardOriginalFilename}</button>
                        ) : (
                            '첨부파일 없음'
                        )}</td>
                    </tr>
                    <tr>
                        <th>등록날짜</th>
                        <td>{board.boardDate}</td>
                    </tr>
                    <tr>
                        <th>내용</th>
                        <td>{board.boardContent}</td>
                    </tr>
                    <tr>
                        <th>조회수</th>
                        <td>{board.boardReadCount}</td>
                    </tr>
                </tbody>
            </table>
            {/* 이전 페이지로 이동 버튼 */}
            <div className={styles.buttonGroup}>
                <button className={styles.actionButton} onClick={() => navigate(-1)}>이전 페이지로 이동</button>
            
            {/* 본인 글일 때만 수정과 삭제 표시, 본인 글이 아니면 댓글달기 표시 */}
            {isLoggedIn && userid === board.boardWriter ? (
                <>
                    <button onClick={handleMoveEdit}>수정 페이지로 이동</button>
                    <button onClick={() => handleDelete(board.renameFilePath)}>삭제하기</button>
                </> ) : (isLoggedIn && (                   
                        <button onClick={() => openModal({ boardNum: board.boardNum })}>댓글달기</button>                    
                ))
            }
            </div>
            <h3>댓글</h3>
            <table className={styles.replyTable}>
                <thead>
                    <tr>
                        <th>작성자</th>
                        <th>제목</th>
                        <th>내용</th>
                        <th>조회수</th>
                        <th>작성날짜</th>
                        <th>수정|삭제</th>
                    </tr>
                </thead>
                <tbody>
                    {replies.map((reply) => (
                        <tr key={reply.replyNum} 
                            className={reply.replyLev === 2 ? styles.replyIndented : styles.replyItem}>
                            <td>{reply.replyWriter}</td>
                            <td>
                                {editingReply === reply.replyNum ? (
                                    <input type="text"
                                        value={editingTitle}
                                        onChange={(e) => setEditingTitle(e.target.value)} />
                                ) : (
                                    reply.replyTitle
                                )}
                            </td>
                            <td>
                                {editingReply === reply.replyNum ? (
                                    <input type="text"
                                        value={editingContent}
                                        onChange={(e) => setEditingContent(e.target.value)} />
                                ) : (
                                    reply.replyContent
                                )}                               
                            </td>
                            <td>{reply.replyReadCount}</td>
                            <td>{reply.replyDate}</td>
                            <td>
                                {isLoggedIn && userid === reply.replyWriter ? (
                                    // 댓글 또는 대댓글 작성자와 로그인 사용자가 동일할 경우 수정 및 삭제 버튼 표시
                                    editingReply === reply.replyNum ? (
                                        <button onClick={() => handleSaveReplyEdit(reply.replyNum)}>저장</button>
                                    ) : (
                                    <>
                                        <button onClick={() => handleReplyEdit(reply.replyNum, reply.replyTitle, reply.replyContent)}>수정</button>
                                        <button onClick={() => handleReplyDelete(reply.replyNum)}>삭제</button>
                                    </>
                                    )) : (
                                        // replyLev 이 1인 댓글에만 대댓글달기 버튼 표시
                                        isLoggedIn && reply.replyLev === 1 && (
                                            <div>
                                                <button onClick={() => openModal({ boardNum: board.boardNum, replyNum: reply.replyNum })}>대댓글달기</button>
                                            </div>
                                        )
                                    )
                                }
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

            {/* 댓글|대댓글 등록 모달창 */}
            {showModal && (
                <Modal onClose={closeModal}>
                    <ReplyWrite  boardNum={replyTarget.boardNum}
                        replyNum={replyTarget.replyNum}
                        onReplyAdded={closeModal} />
                </Modal>
            )}

        </div>
    );

};  // const BoardDetail

export default BoardDetail;