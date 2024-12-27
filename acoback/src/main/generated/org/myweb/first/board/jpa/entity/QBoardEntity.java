package org.myweb.first.board.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBoardEntity is a Querydsl query type for BoardEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoardEntity extends EntityPathBase<BoardEntity> {

    private static final long serialVersionUID = -1667367545L;

    public static final QBoardEntity boardEntity = new QBoardEntity("boardEntity");

    public final StringPath boardContent = createString("boardContent");

    public final DatePath<java.sql.Date> boardDate = createDate("boardDate", java.sql.Date.class);

    public final NumberPath<Integer> boardNum = createNumber("boardNum", Integer.class);

    public final StringPath boardOriginalFilename = createString("boardOriginalFilename");

    public final NumberPath<Integer> boardReadCount = createNumber("boardReadCount", Integer.class);

    public final StringPath boardRenameFilename = createString("boardRenameFilename");

    public final StringPath boardTitle = createString("boardTitle");

    public final StringPath boardWriter = createString("boardWriter");

    public QBoardEntity(String variable) {
        super(BoardEntity.class, forVariable(variable));
    }

    public QBoardEntity(Path<? extends BoardEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBoardEntity(PathMetadata metadata) {
        super(BoardEntity.class, metadata);
    }

}

