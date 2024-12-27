package org.myweb.first.board.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.board.model.dto.Board;
import org.myweb.first.board.model.dto.Reply;
import org.myweb.first.board.model.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Date;

@Slf4j    //log 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@RequiredArgsConstructor
@RestController
@RequestMapping("/reply")
@CrossOrigin
public class ReplyController {
    @Autowired
    private ReplyService replyService;

    //요청 처리용 ---------------------------------------------------------------

    //게시 댓글, 대댓글 등록 처리용
    @PostMapping()
    public ResponseEntity replyInsertMethod(@ModelAttribute Reply reply) {
        log.info("replyInsertMethod : " + reply);

        //원글에 대한 댓글이므로 레벨을 1로 지정함
//        if(reply.getReplyReplyRef() == 0) {  //참조댓글번호가 없다면
//            reply.setReplyLev(1);  // 댓글로 처리
//        }else if(reply.getReplyReplyRef() != 0) {  //참조댓글번호가 있다면
//            reply.setReplyLev(2);   // 대댓글로 처리
//        }

        if(replyService.insertReply(reply) != null) {
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //댓글과 대댓글 수정 처리용
    @PutMapping("/{replyNum}")
    public ResponseEntity replyUpdateMethod(@RequestBody Reply reply) {
        // json 형태로 보내는 값 : @RequestBody
        // formData 로 보내는 값 : @ModelAttribute
        log.info("replyUpdateMethod : " + reply);
        //수정날짜로 변경 처리
        reply.setReplyDate(new Date(System.currentTimeMillis()));

        if(replyService.updateReply(reply) > 0) {
            //댓글과 대댓글 수정 성공시 다시 상세보기가 보여지게 처리
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //댓글과 대댓글 삭제 처리용
    @DeleteMapping("/{replyNum}")
    public ResponseEntity replyDeleteMethod(
            @PathVariable int replyNum) {
        log.info("replyDeleteMethod : " + replyNum);

        if(replyService.deleteReply(replyNum) > 0) {
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
