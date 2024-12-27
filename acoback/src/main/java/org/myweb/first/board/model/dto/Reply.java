package org.myweb.first.board.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.board.jpa.entity.ReplyEntity;

@Data    //@Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reply {
    private int replyNum;       //REPLY_NUM	NUMBER
    private String replyWriter;     //REPLY_WRITER	VARCHAR2(50 BYTE)
    private String replyTitle;      //REPLY_TITLE	VARCHAR2(50 BYTE)
    private String replyContent;    //REPLY_CONTENT	VARCHAR2(2000 BYTE)
    private int boardRef;		  //BOARD_REF	NUMBER
    private int replyReplyRef;    //REPLY_REPLY_REF	NUMBER
    private int replyLev;       //REPLY_LEV	NUMBER
    private int replySeq;       //REPLY_SEQ	NUMBER
    private int replyReadCount; //REPLY_READCOUNT	NUMBER
    @JsonFormat(pattern = "yyyy-MM-dd")
    private java.sql.Date replyDate;   //REPLY_DATE	DATE

    //dto --> entity 로 변환하는 메소드 추가함
    public ReplyEntity toEntity() {
        return ReplyEntity.builder()
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
