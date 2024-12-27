package org.myweb.first.board.jpa.entity;

public interface BoardNativeVo {
    // board_num, board_title, board_readcount 컬럼에 대한 get 메소드 작성
    int getBoard_num();
    String getBoard_title();
    int getBoard_readcount();
}
