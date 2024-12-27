package org.myweb.first.member.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.member.model.dto.Member;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "MEMBER") //매핑할 테이블 이름 지정함, NOTICE 테이블을 자동으로 만들어 주기도 하는 어노테이션임
@Entity
public class MemberEntity {
    @Id
    @Column(name = "USERID", nullable = false)
    private String userId;  //USERID	VARCHAR2(50 BYTE)
    @Column(name = "USERPWD")
    private String userPwd; //USERPWD	VARCHAR2(100 BYTE)
    @Column(name = "USERNAME", nullable = false)
    private String userName; //USERNAME	VARCHAR2(20 BYTE)
    @Column(name = "GENDER", nullable = false)
    private String gender;  //GENDER	CHAR(1 BYTE)
    @Column(name = "AGE", nullable = false)
    private int age;   //AGE	NUMBER(3,0)
    @Column(name = "PHONE")
    private String phone;  //PHONE	VARCHAR2(13 BYTE)
    @Column(name = "EMAIL", nullable = false)
    private String email;   //EMAIL	VARCHAR2(30 BYTE)
    @Column(name = "ENROLL_DATE", nullable = false)
    private Date enrollDate;  //ENROLL_DATE	DATE
    @Column(name = "LASTMODIFIED", nullable = false)
    private Date lastModified;  //LASTMODIFIED	DATE
    @Column(name = "SIGNTYPE", nullable = false, columnDefinition = "direct")
    private String signType;  //SIGNTYPE	VARCHAR2(10 BYTE)
    @Column(name = "ADMIN_YN", nullable = false, columnDefinition = "N")
    private String adminYN;  //ADMIN_YN	CHAR(1 BYTE)
    @Column(name = "LOGIN_OK", nullable = false, columnDefinition = "Y")
    private String loginOk;  //LOGIN_OK	CHAR(1 BYTE)
    @Column(name = "PHOTO_FILENAME")
    private String photoFileName;  //PHOTO_FILENAME	VARCHAR2(100 BYTE)

    @PrePersist     //jpa 로 넘어가기 전(sql 에 적용하기 전)에 작동된다는 어노테이션임
    public void prePersist(){
        //insert 문 실행시 주로 사용됨
        enrollDate = new Date(System.currentTimeMillis());  //현재 날짜 시간 적용
        lastModified = new Date(System.currentTimeMillis());  //현재 날짜 시간 적용
    }

    public Member toDto(){
        return Member.builder()
                .userId(userId)
                .userPwd(userPwd)
                .userName(userName)
                .gender(gender)
                .age(age)
                .phone(phone)
                .email(email)
                .enrollDate(enrollDate)
                .lastModified(lastModified)
                .signType(signType)
                .adminYN(adminYN)
                .loginOk(loginOk)
                .photoFileName(photoFileName)
                .build();
    }
}
