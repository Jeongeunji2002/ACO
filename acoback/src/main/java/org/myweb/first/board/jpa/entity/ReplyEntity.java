package org.myweb.first.board.jpa.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.board.model.dto.Reply;

import java.util.GregorianCalendar;

@Data    //@Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="REPLY")  //매핑할 테이블 이름 지정함
@Entity //JPA 가 관리함, 테이블의 컬럼과 DTO 클래스의 프로퍼티를 매핑하는 역할을 함
public class ReplyEntity {
    @Id  //JPA가 객체를 관리할 때 식별할 기본키 지정
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //primary key 지정에 사용함, dml 실행시 default 를 사용하므로 지정하지 말 것
    @Column(name="REPLY_NUM", nullable = false)
    private int replyNum;       //REPLY_NUM	NUMBER
    @Column(name="REPLY_WRITER", nullable = false, length = 50)
    private String replyWriter;     //REPLY_WRITER	VARCHAR2(50 BYTE)
    @Column(name="REPLY_TITLE", nullable = false, length = 50)
    private String replyTitle;      //REPLY_TITLE	VARCHAR2(50 BYTE)
    @Column(name="REPLY_CONTENT", nullable = false, length = 2000)
    private String replyContent;    //REPLY_CONTENT	VARCHAR2(2000 BYTE)
    @Column(name="BOARD_REF")
    private int boardRef;		  //BOARD_REF	NUMBER
    @Column(name="REPLY_REPLY_REF")
    private int replyReplyRef;    //REPLY_REPLY_REF	NUMBER
    @Column(name="REPLY_LEV", columnDefinition = "1")
    private int replyLev;       //REPLY_LEV	NUMBER
    @Column(name="REPLY_SEQ", columnDefinition = "1")
    private int replySeq;       //REPLY_SEQ	NUMBER
    @Column(name="REPLY_READCOUNT", columnDefinition = "1")
    private int replyReadCount; //REPLY_READCOUNT	NUMBER
    @Column(name="REPLY_DATE")
    private java.sql.Date replyDate;   //REPLY_DATE	DATE

    @PrePersist  //jpa 로 넘어가지 전에 작동
    public void prePersist() {
        replyDate = new java.sql.Date(System.currentTimeMillis());   //boardDate 에 현재 시간 적용
    }

    //entity --> dto 로 변환하는 메소드 추가함
    public Reply toDto(){
        return Reply.builder()
                .replyNum(replyNum)
                .replyWriter(replyWriter)
                .replyTitle(replyTitle)
                .replyContent(replyContent)
                .boardRef(boardRef)
                .replyReplyRef(replyReplyRef)
                .replyLev(replyLev)
                .replySeq(replySeq)
                .replyReadCount(replyReadCount)
                .replyDate(replyDate)
                .build();
    }
}
