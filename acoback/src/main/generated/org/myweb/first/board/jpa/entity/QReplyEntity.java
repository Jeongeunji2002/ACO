package org.myweb.first.board.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReplyEntity is a Querydsl query type for ReplyEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReplyEntity extends EntityPathBase<ReplyEntity> {

    private static final long serialVersionUID = -139915637L;

    public static final QReplyEntity replyEntity = new QReplyEntity("replyEntity");

    public final NumberPath<Integer> boardRef = createNumber("boardRef", Integer.class);

    public final StringPath replyContent = createString("replyContent");

    public final DatePath<java.sql.Date> replyDate = createDate("replyDate", java.sql.Date.class);

    public final NumberPath<Integer> replyLev = createNumber("replyLev", Integer.class);

    public final NumberPath<Integer> replyNum = createNumber("replyNum", Integer.class);

    public final NumberPath<Integer> replyReadCount = createNumber("replyReadCount", Integer.class);

    public final NumberPath<Integer> replyReplyRef = createNumber("replyReplyRef", Integer.class);

    public final NumberPath<Integer> replySeq = createNumber("replySeq", Integer.class);

    public final StringPath replyTitle = createString("replyTitle");

    public final StringPath replyWriter = createString("replyWriter");

    public QReplyEntity(String variable) {
        super(ReplyEntity.class, forVariable(variable));
    }

    public QReplyEntity(Path<? extends ReplyEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReplyEntity(PathMetadata metadata) {
        super(ReplyEntity.class, metadata);
    }

}

