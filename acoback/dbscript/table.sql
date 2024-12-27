DROP TABLE USERS CASCADE CONSTRAINTS;
DROP TABLE MEMBER CASCADE CONSTRAINTS;
DROP TABLE NOTICE CASCADE CONSTRAINTS;
DROP TABLE BOARD CASCADE CONSTRAINTS;
DROP TABLE REPLY CASCADE CONSTRAINTS;

-- 소셜로그인시 회원가입을 이용할 경우의 회원 테이블
CREATE TABLE MEMBER(
  USERID VARCHAR2(50) CONSTRAINT PK_MEMBER_UID PRIMARY KEY,
  USERPWD VARCHAR2(100),
  USERNAME VARCHAR2(20) NOT NULL,
  GENDER CHAR(1)  NOT NULL,
  AGE NUMBER(3)   NOT NULL,
  PHONE VARCHAR2(13),
  EMAIL VARCHAR2(30) not null,  
  ENROLL_DATE DATE DEFAULT SYSDATE,
  LASTMODIFIED DATE DEFAULT SYSDATE,
  signtype VARCHAR2(10) default 'direct' not null,
  -- 가입방식 : 직접 가입('direct'), 소셜로그인('kakao', 'naver', 'google') 
  ADMIN_YN CHAR(1) DEFAULT 'N' not null,
  login_ok CHAR(1) DEFAULT 'Y' not null,
  photo_filename  varchar2(100)
  );

comment on column member.userid is '회원아이디';
comment on column member.userpwd is '회원패스워드';
comment on column member.username is '회원이름';
comment on column member.gender is '회원성별';
comment on column member.age is '회원나이';
comment on column member.email is '회원이메일';
comment on column member.phone is '회원전화번호';
comment on column member.enroll_date is '회원가입날짜';
comment on column member.userid is '회원아이디';
comment on column member.lastmodified is '마지막수정날짜';
comment on column member.signtype is '가입방식';
comment on column member.login_ok is '로그인가능여부';
comment on column member.admin_yn is '관리자여부';
comment on column member.photo_filename is '회원사진파일명';

CREATE TABLE USERS (
  USERID VARCHAR2(15) PRIMARY KEY,
  USERPWD VARCHAR2(100) NOT NULL,
  USERNAME VARCHAR2(20) NOT NULL,
  ROLE	VARCHAR2(30) DEFAULT 'MEMBER' NOT NULL
);

ALTER TABLE USERS
DROP PRIMARY KEY;

-- 삭제룰을 추가한 새 제약조건 추가함
ALTER TABLE USERS
ADD CONSTRAINT FK_MEMBER_UID 
FOREIGN KEY(USERID) REFERENCES MEMBER ON DELETE CASCADE;

-- TRIGGER 작성 : 이름 - TRI_INSERT_USERS
-- MEMBER 테이블에 새 회원정보가 기록되면, 자동으로 USERS 테이블에 아이디, 암호, 이름, ROLL이
-- INSERT 되게 함
CREATE OR REPLACE TRIGGER TRI_INSERT_USERS
AFTER INSERT ON MEMBER
FOR EACH ROW
BEGIN
  INSERT INTO USERS
  VALUES (:NEW.USERID, :NEW.USERPWD, :NEW.USERNAME, DEFAULT);  

  IF :NEW.ADMIN_YN = 'Y' THEN
  	UPDATE USERS
	SET ROLE = 'ADMIN'
	WHERE USERID = :NEW.USERID;
  END IF;  
  
END;
/

CREATE OR REPLACE TRIGGER TRI_UPDATE_USERS
AFTER UPDATE ON MEMBER
FOR EACH ROW
BEGIN
	IF :NEW.USERPWD != :OLD.USERPWD THEN
	  UPDATE USERS 
	  SET USERPWD = :NEW.USERPWD		  
	  WHERE USERID = :OLD.USERID;	
    END IF;
    
    IF :NEW.ADMIN_YN = 'Y' THEN
	  UPDATE USERS 
	  SET ROLE = 'ADMIN'		  
	  WHERE USERID = :OLD.USERID;	
    END IF;
    
    IF :NEW.ADMIN_YN = 'N' THEN
	  UPDATE USERS 
	  SET ROLE = 'MEMBER'		  
	  WHERE USERID = :OLD.USERID;	
    END IF;
	
END;
/

INSERT INTO MEMBER VALUES ('admin', 'admin', '관리자', 'M', 35, '010-1111-9999', 
'admin@ncs.kr', to_date('2016-06-25', 'RRRR-MM-DD'), sysdate, default, 'Y', default, null);

INSERT INTO MEMBER
VALUES ('user01', 'pass01', '홍길동', 'M', 25, '010-1234-5678', 'hong1234@ncs.kr', 
        default, default, default, default, default, null);
        
INSERT INTO MEMBER
VALUES ('user02', 'pass02', '신사임당', 'F', 45, '010-4545-9999', 'dano99@ncs.kr', 
        default, default, 'direct', default, default, null);
        

COMMIT;

SELECT * FROM MEMBER;
SELECT * FROM USERS;

-- notice 테이블
CREATE TABLE NOTICE(
  NOTICENO NUMBER CONSTRAINT PK_NOTICENO PRIMARY KEY,
  NOTICETITLE VARCHAR2(50) NOT NULL,
  NOTICEDATE DATE DEFAULT SYSDATE,
  NOTICEWRITER VARCHAR2(50) NOT NULL,
  NOTICECONTENT VARCHAR2(2000),
  ORIGINAL_FILEPATH VARCHAR2(100),
  RENAME_FILEPATH VARCHAR2(100),
  IMPORTANCE CHAR(1) DEFAULT 'N' NOT NULL,  
  IMP_END_DATE DATE DEFAULT SYSDATE NOT NULL,
  READCOUNT NUMBER DEFAULT 1 NOT NULL,
  CONSTRAINT FK_NOTICEWRITER FOREIGN KEY (NOTICEWRITER) 
      REFERENCES MEMBER ON DELETE CASCADE
);

COMMENT ON COLUMN notice.NOTICENO IS '공지글번호';
COMMENT ON COLUMN notice.NOTICETITLE IS '공지글제목';
COMMENT ON COLUMN notice.NOTICEDATE IS '공지등록날짜';
COMMENT ON COLUMN notice.NOTICEWRITER IS '공지작성자';
COMMENT ON COLUMN notice.NOTICECONTENT IS '공지내용';
COMMENT ON COLUMN notice.ORIGINAL_FILEPATH IS '원본첨부파일명';
COMMENT ON COLUMN notice.RENAME_FILEPATH IS '바뀐첨부파일명';
COMMENT ON COLUMN notice.IMPORTANCE IS '중요도';
COMMENT ON COLUMN notice.IMP_END_DATE IS '중요도게시종료날짜';
COMMENT ON COLUMN notice.READCOUNT IS '공지조회수';

COMMIT;

INSERT INTO NOTICE VALUES (1, '공지 서비스 오픈', SYSDATE, 'admin', 
'공지 서비스가 오픈되었습니다. 많이 이용해 주세요.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), 
'공지 서비스 오픈2', SYSDATE, 'admin', 
'공지 서비스가 오픈되었습니다. 많이 이용해 주세요.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '공지 서비스 오픈3', SYSDATE, 'admin', 
'공지 서비스가 오픈되었습니다. 많이 이용해 주세요.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '공지 서비스 오픈4', SYSDATE, 'admin', 
'공지 서비스가 오픈되었습니다. 많이 이용해 주세요.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '공지 서비스 오픈5', SYSDATE, 'admin', 
'공지 서비스가 오픈되었습니다. 많이 이용해 주세요.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '공지 서비스 오픈6', SYSDATE, 'admin', 
'공지 서비스가 오픈되었습니다. 많이 이용해 주세요.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '공지 서비스 오픈7', SYSDATE, 'admin', 
'공지 서비스가 오픈되었습니다. 많이 이용해 주세요.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '공지 서비스 오픈8', SYSDATE, 'admin', 
'공지 서비스가 오픈되었습니다. 많이 이용해 주세요.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '공지 서비스 오픈9', SYSDATE, 'admin', 
'공지 서비스가 오픈되었습니다. 많이 이용해 주세요.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '공지 서비스 오픈10', SYSDATE, 'admin', 
'공지 서비스가 오픈되었습니다. 많이 이용해 주세요.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '신입사원 모집공고', 
TO_DATE('2016-07-15', 'RRRR-MM-DD'), 'admin', 
'공지 서비스가 오픈되었습니다. 많이 이용해 주세요.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '신입사원 모집공고 마감', 
TO_DATE('2016-07-20', 'RRRR-MM-DD'), 'admin', 
'2016년 7월 20일 18시에 신입사원 모집을 마감합니다.', null, null, DEFAULT, DEFAULT, DEFAULT);

SELECT * FROM NOTICE;
 
commit;



-- BOARD TABLE
CREATE TABLE BOARD(
	BOARD_NUM	NUMBER,	
	BOARD_WRITER	 VARCHAR2(50) NOT NULL,
	BOARD_TITLE	VARCHAR2(50) NOT NULL,
	BOARD_CONTENT	VARCHAR2(2000) NOT NULL,
	BOARD_ORIGINAL_FILENAME	VARCHAR2(100),
    BOARD_RENAME_FILENAME VARCHAR2(100),    
	BOARD_READCOUNT	NUMBER DEFAULT 0,
	BOARD_DATE	DATE DEFAULT SYSDATE,
  CONSTRAINT PK_BOARD PRIMARY KEY (BOARD_NUM),
  CONSTRAINT FK_BOARD_WRITER FOREIGN KEY (BOARD_WRITER) REFERENCES MEMBER (USERID) ON DELETE CASCADE
);

COMMENT ON COLUMN BOARD.BOARD_NUM IS '게시글번호';
COMMENT ON COLUMN BOARD.BOARD_WRITER IS '작성자아이디';
COMMENT ON COLUMN BOARD.BOARD_TITLE IS '게시글제목';
COMMENT ON COLUMN BOARD.BOARD_CONTENT IS '게시글내용';
COMMENT ON COLUMN BOARD.BOARD_DATE IS '작성날짜';
COMMENT ON COLUMN BOARD.BOARD_ORIGINAL_FILENAME IS '원본첨부파일명';
COMMENT ON COLUMN BOARD.BOARD_RENAME_FILENAME IS '바뀐첨부파일명';
COMMENT ON COLUMN BOARD.BOARD_READCOUNT IS '읽은조회수';

CREATE TABLE REPLY (
	REPLY_NUM	NUMBER,	
	REPLY_WRITER	 VARCHAR2(50) NOT NULL,
	REPLY_TITLE	VARCHAR2(50) NOT NULL,
	REPLY_CONTENT	VARCHAR2(2000) NOT NULL,	
    BOARD_REF NUMBER,
	REPLY_REPLY_REF	NUMBER,
	REPLY_LEV	NUMBER DEFAULT 1,
	REPLY_SEQ NUMBER DEFAULT 1,
	REPLY_READCOUNT	NUMBER DEFAULT 1,
	REPLY_DATE	DATE DEFAULT SYSDATE,
  CONSTRAINT PK_REPLY PRIMARY KEY (REPLY_NUM),
  CONSTRAINT FK_REPLY_WRITER FOREIGN KEY (REPLY_WRITER) REFERENCES MEMBER (USERID) ON DELETE CASCADE,
  CONSTRAINT FK_BOARD_REF FOREIGN KEY (BOARD_REF) REFERENCES BOARD (BOARD_NUM) ON DELETE CASCADE,
  CONSTRAINT FK_RREPLY_REF FOREIGN KEY (REPLY_REPLY_REF) REFERENCES REPLY (REPLY_NUM) ON DELETE CASCADE
);

COMMENT ON COLUMN REPLY.REPLY_NUM IS '댓글번호';
COMMENT ON COLUMN REPLY.REPLY_WRITER IS '댓글작성자아이디';
COMMENT ON COLUMN REPLY.REPLY_TITLE IS '댓글제목';
COMMENT ON COLUMN REPLY.REPLY_CONTENT IS '댓글내용';
COMMENT ON COLUMN REPLY.REPLY_DATE IS '댓글작성날짜';
COMMENT ON COLUMN REPLY.BOARD_REF IS '원글번호';  -- 원글번호 
COMMENT ON COLUMN REPLY.REPLY_REPLY_REF IS '참조답글번호';  -- 원글의 답/댓글 : 자기번호, 답글의 답글 : 참조댓/답글번호
COMMENT ON COLUMN REPLY.REPLY_LEV IS '답글단계'; -- 원글의 답글 : 1, 답글의 답글 : 2
COMMENT ON COLUMN REPLY.REPLY_SEQ IS '답글순번'; -- 같은 원글의 답글일 때 : 1, 2, 3, ....... 순차처리 (최신글이 1)
COMMENT ON COLUMN REPLY.REPLY_READCOUNT IS '읽은조회수';

INSERT INTO BOARD 
VALUES (1, 'admin', '관리자 게시글', '저희 사이트를 이용해 주셔서 감사합니다.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (2, 'user01', 'MVC Model2', '웹 어플리케이션 설계 방식 중 MVC 디자인 패턴 모델2 방식의 한 유형입니다.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (3, 'user02', '설계방식2', '설계방식 2번째로는 First Controller 를 사용하는 방식이 있습니다.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (4, 'user01', '설계방식3', '3번째 방식은 액션을 이용하는 방식입니다.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (5, 'user01', 'MVC Model2', '웹 어플리케이션 설계 방식 중 MVC 디자인 패턴 모델2 방식의 한 유형입니다.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (6, 'user02', '설계방식2', '설계방식 2번째로는 First Controller 를 사용하는 방식이 있습니다.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (7, 'user01', '설계방식3', '3번째 방식은 액션을 이용하는 방식입니다.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (8, 'user01', 'MVC Model2', '웹 어플리케이션 설계 방식 중 MVC 디자인 패턴 모델2 방식의 한 유형입니다.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (9, 'user02', '설계방식2', '설계방식 2번째로는 First Controller 를 사용하는 방식이 있습니다.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (10, 'user01', '설계방식3', '3번째 방식은 액션을 이용하는 방식입니다.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (11, 'user01', 'MVC Model2', '웹 어플리케이션 설계 방식 중 MVC 디자인 패턴 모델2 방식의 한 유형입니다.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (12, 'user02', '설계방식2', '설계방식 2번째로는 First Controller 를 사용하는 방식이 있습니다.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (13, 'user01', '설계방식3', '3번째 방식은 액션을 이용하는 방식입니다.', 
NULL, NULL, DEFAULT, DEFAULT);

SELECT * FROM BOARD;

COMMIT;

-- board 테이블 조회수 수정 처리
update board
set board_readcount = 123
where board_num = 7;

update board
set board_readcount = 77
where board_num = 3;

update board
set board_readcount = 12
where board_num = 10;

COMMIT;


-- REPLY 테이블 데이터 INSERT
INSERT INTO REPLY 
VALUES (1, 'user01', '관리자 게시글 댓글', 
'저희 사이트를 이용해 주셔서 감사합니다. 댓글입니다. 댓글입니다.', 
1, 1, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (2, 'user02', 'MVC Model2 댓글', 
'웹 어플리케이션 설계 방식 중 MVC 디자인 패턴 모델2 방식의 한 유형입니다. 댓글입니다.', 
2, 2, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (3, 'user01', '설계방식2 댓글', 
'설계방식 2번째로는 First Controller 를 사용하는 방식이 있습니다. 댓글입니다.', 
3, 3, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (4, 'user02', '설계방식3 댓글', 
'3번째 방식은 액션을 이용하는 방식입니다. 댓글입니다.', 
4, 4, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (5, 'user02', 'MVC Model2 댓글', 
'웹 어플리케이션 설계 방식 중 MVC 디자인 패턴 모델2 방식의 한 유형입니다. 댓글입니다.', 
5, 5, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (6, 'admin', '설계방식2 댓글', 
'설계방식 2번째로는 First Controller 를 사용하는 방식이 있습니다. 댓글입니다.', 
6, 6, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (7, 'user02', '설계방식3 댓글', 
'3번째 방식은 액션을 이용하는 방식입니다. 댓글입니다.', 
7, 7, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (8, 'user02', 'MVC Model2 댓글', 
'웹 어플리케이션 설계 방식 중 MVC 디자인 패턴 모델2 방식의 한 유형입니다. 댓글입니다.', 
8, 8, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (9, 'user01', '설계방식2 댓글', 
'설계방식 2번째로는 First Controller 를 사용하는 방식이 있습니다. 댓글입니다.', 
9, 9, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (10, 'user02', '설계방식3 댓글', 
'3번째 방식은 액션을 이용하는 방식입니다. 댓글입니다.', 
10, 10, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (11, 'user02', 'MVC Model2 댓글', 
'웹 어플리케이션 설계 방식 중 MVC 디자인 패턴 모델2 방식의 한 유형입니다. 댓글입니다.', 
11, 11, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (12, 'user01', '설계방식2 댓글', 
'설계방식 2번째로는 First Controller 를 사용하는 방식이 있습니다. 댓글입니다.', 
12, 12, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (13, 'admin', '설계방식3 댓글', 
'3번째 방식은 액션을 이용하는 방식입니다. 댓글입니다.', 
13, 13, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

commit;

SELECT * FROM REPLY;

