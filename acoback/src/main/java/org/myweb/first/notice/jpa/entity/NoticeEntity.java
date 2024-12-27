package org.myweb.first.notice.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.notice.model.dto.Notice;

import java.sql.Date;

//테이블 생성에 대한 가이드 클래스임
//@Entity 어노테이션 반드시 표시함 => 설정정보에 자동 등록됨 => Repository 와 연결됨

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "NOTICE") //매핑할 테이블 이름 지정함, NOTICE 테이블을 자동으로 만들어 주기도 하는 어노테이션임
@Entity     // JPA 가 관리함, DB 테이블과 DTO 클래스 매핑을 위해서 필요함
public class NoticeEntity {
    @Id     //JPA 가 Entity 객체들을 관리할 때 식별할 아이디 생성 용도의 어노테이션임
    //@GeneratedValue(strategy = GenerationType.IDENTITY)  //테이블 자동으로 만들어지게 할 때, 기본키 지정에 사용함
    @Column(name = "NOTICENO", nullable = false)
    private int noticeNo;			//NOTICENO	NUMBER
    @Column(name = "NOTICETITLE", nullable = false)
    private String noticeTitle;		//NOTICETITLE	VARCHAR2(50 BYTE)
    @Column(name = "NOTICEDATE")
    private Date noticeDate;	//NOTICEDATE	DATE
    @Column(name = "NOTICEWRITER")
    private String noticeWriter;		//NOTICEWRITER	VARCHAR2(50 BYTE)
    @Column(name = "NOTICECONTENT")
    private String noticeContent;		//NOTICECONTENT	VARCHAR2(2000 BYTE)
    @Column(name = "ORIGINAL_FILEPATH")
    private String originalFilePath;	//ORIGINAL_FILEPATH	VARCHAR2(100 BYTE)
    @Column(name = "RENAME_FILEPATH")
    private String renameFilePath;	//RENAME_FILEPATH	VARCHAR2(100 BYTE)
    @Column(name = "IMPORTANCE", nullable = false, columnDefinition = "N")
    private String importance;		//IMPORTANCE	CHAR(1 BYTE)
    @Column(name = "IMP_END_DATE", nullable = false)
    private Date impEndDate;	//IMP_END_DATE	DATE
    @Column(name = "READCOUNT", nullable = false, columnDefinition = "1")
    private int readCount;			//READCOUNT	NUMBER

    @PrePersist     //jpa 로 넘어가기 전(sql 에 적용하기 전)에 작동된다는 어노테이션임
    public void prePersist(){
        noticeDate = new Date(System.currentTimeMillis());  //현재 날짜 시간 적용
        //공지사항 중요도 종료날짜를 자동으로 등록날짜 기준 3일 뒤로 한다면
        //impEndDate = new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 3));
    }

    public Notice toDto() {
        return Notice.builder()
                .noticeNo(noticeNo)
                .noticeTitle(noticeTitle)
                .noticeDate(noticeDate)
                .noticeWriter(noticeWriter)
                .noticeContent(noticeContent)
                .originalFilePath(originalFilePath)
                .renameFilePath(renameFilePath)
                .importance(importance)
                .impEndDate(impEndDate)
                .readCount(readCount)
                .build();
    }
}
