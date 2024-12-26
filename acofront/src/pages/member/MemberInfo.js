// src/pages/member/MemberInfo.js
// '/member/myinfo/{userid}' 요청시 로그인한 회원정보 출력 페이지
// 첨부된 사진파일은 미리보기 처리함, 사진파일명 클릭시 다운로드 처리함
// 회원정보 출력값들을 모두 input 의 value 로 적용해서 랜더링하게 함 => 수정도 동시에 할 수 있도록 함
// form 에 출력처리함 => 수정하기(submit) 버튼과 탈퇴하기(/member/{userid} delete | put 요청) 버튼 제공
import React, { useState, useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../../AuthProvider";
import apiClient from "../../utils/axios";
import styles from "./MemberInfo.module.css";

const MemberInfo = () => {
    const { userid, accessToken, logout } = useContext(AuthContext);
    const [member, setMember] = useState(null);
    const [userPwd, setUserPwd] = useState('');
    const [userPwd2, setUserPwd2] = useState('');
    const [photoFile, setPhotoFile] = useState(null);
    const [previewPhoto, setPreviewPhoto] = useState('');

    const navigate = useNavigate();  //useNavigate 훅 사용 선언
    
    useEffect(() => {
        const fetchMemberInfo = async () => {
            try {
                const response = await apiClient.get(`/member/myinfo/${userid}`, {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                });
                setMember(response.data);
                // 서버에서 반환된 photoFileName이 있을 경우 URL로 변환
                if (response.data.photoFileName) {
                    const photoUrl = `http://localhost:8080/photo/${response.data.photoFileName}`;
                    setPreviewPhoto(photoUrl);
                }
            } catch (error) {
                console.error('회원정보 조회 실패! ', error);
            }
        };  //함수 만들기함

        //함수 실행시키기
        if(accessToken && userid){
            fetchMemberInfo();
        }
    }, [accessToken, userid]);  //useEffect();

    // 첨부된 사진 파일이 변경되면 작동되는 핸들러 함수
    const handleFileChange = (e) => {
        const file = e.target.files[0];
        //변경된 사진 파일 미리보기 처리함
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                setPreviewPhoto(e.target.result);
            };
            reader.readAsDataURL(file);
            setPhotoFile(file);
        }
    };

    // 암호 변경시 암호와 암호확인이 일치하는지 확인하는 함수
    const validate = () => {
        if (userPwd !== userPwd2){
            alert('암호와 암호확인이 일치하지 않습니다. 다시 입력하세요.');
            setUserPwd('');
            setUserPwd2('');
            return false;
        }
        return true;
    };

    // input 의 value 가 변경(change)되면 작동되는 핸들러 함수
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setMember((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    // 수정하기(submit) 버튼 클릭시 수정 요청 처리 함수
    const handleSubmit = async (e) => {
        e.preventDefault();  // 전송이 되지않게 submit 이벤트 취소함
        if(validate()){
            try {
                const formData = new FormData();                
                formData.append('userPwd', userPwd);
                formData.append('photofile', photoFile);                
                formData.append("originalUserPwd", member.userPwd);
                formData.append("ofile", member.photoFileName);
                
                Object.entries(member).forEach(([key, value]) => {
                    formData.append(key, value);
                });

                await apiClient.put(`/member/${userid}`, formData, {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                        'Content-Type': 'multipart/form-data',
                    },
                });
                alert('회원정보가 변경되었습니다.');
            } catch (error) {
                console.error('회원정보 수정 요청 실패 : ', error);
                alert('회원정보 수정 요청이 실패했습니다.');
            }
        }
    };

    // 탈퇴하기 버튼 클릭시 delete(put 권장함) 요청 처리 함수
    const handleDelete = async () => {
        if(window.confirm('정말 탈퇴하시겠습니까? 이후 계정이 복구되지 않습니다.')){
            try {
                await apiClient.delete(`/member/${userid}`, {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                });
                alert('회원님의 회원정보와 계정이 삭제되었습니다. 자동 로그아웃됩니다.');
                logout();  // AuthProvider 에서 가져온 logout() 함수 실행
                navigate('/');  //Home.js 로 페이지가 바뀌게 함
            } catch (error) {
                console.error('회원탈퇴 실패 : ', error);
                alert('회원 탈퇴 요청이 실퍃하였습니다. 관리자에게 문의하세요.');
            }
        }
    };

    if (!member) return <p>Loading...</p>;  //member 가 null 이면 로딩중... 출력함

    return (
        <div>
            <h1 style={{ textAlign: 'center'}}>{userid} 님, 프로파일 정보</h1>
            <form onSubmit={handleSubmit} encType="multipart/form-data">                
                <table style={{ margin: 'auto', borderCollapse: 'collapse', width: '700px', border: '2px solid navy' }}>
                    <thead>
                        <th colSpan="2" style={{ backgroundColor: '#9ff'}}>
                            회원님의 정보가 아래에 표시됩니다. 회원님의 정보를 수정하고 '수정하기' 버튼을 누르면 수정됩니다.
                        </th>
                    </thead>
                    <tbody>
                        <tr>
                            <th>아이디</th>
                            <td><input tpe="text" value={member.userId} readOnly /></td>
                        </tr>
                        <tr>
                            <th>사진</th>
                            <td>
                                <div style={{ width: '150px', height: '160px', border: '1px solid navy', marginBottom: '10px'}}>
                                    <img src={previewPhoto} alt="Preview" style={{ width: '100%', height: '100%'}} />
                                </div>
                                <input type="file" id="photofile" name="photofile" onChange={handleFileChange} />
                            </td>
                        </tr>
                        <tr>
                            <th>이름</th>
                            <td><input type="text" value={member.userName} readOnly /></td>
                        </tr>
                        <tr>
                            <th>암호</th>
                            <td><input type="password" value={userPwd} onChange={(e) => setUserPwd(e.target.value)} /></td>
                        </tr>
                        <tr>
                            <th>임호확인</th>
                            <td><input type="password" value={userPwd2} onChange={(e) => setUserPwd2(e.target.value)} /></td>
                        </tr>
                        <tr>
                            <th>성별</th>
                            <td>
                                <label><input type="radio" name="gender" value="M" 
                                defaultChecked={member.gender === 'M'} onChange={handleInputChange} /> 남자</label>
                                <label><input type="radio" name="gender" value="F" 
                                defaultChecked={member.gender === 'F'} onChange={handleInputChange} /> 여자</label>
                            </td>
                        </tr>
                        <tr>
                            <th>나이</th>
                            <td><input type="number" name="age" min="19" defaultValue={member.age} onChange={handleInputChange} /></td>
                        </tr>
                        <tr>
                            <th>이메일</th>
                            <td><input type="email" name="email" defaultValue={member.email} onChange={handleInputChange} /></td>
                        </tr>
                        <tr>
                            <th>전화번호</th>
                            <td><input type="tel" name="phone" defaultValue={member.phone} onChange={handleInputChange} /></td>
                        </tr>
                        <tr>
                            <th colSpan="2" style={{ textAlign: 'center'}}>
                                <button type="submit">수정하기</button>   
                                <button type="button" onClick={handleDelete} style={{ marginLeft: '10px' }}>탈퇴하기</button> 
                            </th>                            
                        </tr>
                    </tbody>
                </table>
            </form>
        </div>
    );
};  //MemberInfo()

export default MemberInfo;
