package org.myweb.first.member.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMemberEntity is a Querydsl query type for MemberEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberEntity extends EntityPathBase<MemberEntity> {

    private static final long serialVersionUID = -1783332401L;

    public static final QMemberEntity memberEntity = new QMemberEntity("memberEntity");

    public final StringPath adminYN = createString("adminYN");

    public final NumberPath<Integer> age = createNumber("age", Integer.class);

    public final StringPath email = createString("email");

    public final DatePath<java.sql.Date> enrollDate = createDate("enrollDate", java.sql.Date.class);

    public final StringPath gender = createString("gender");

    public final DatePath<java.sql.Date> lastModified = createDate("lastModified", java.sql.Date.class);

    public final StringPath loginOk = createString("loginOk");

    public final StringPath phone = createString("phone");

    public final StringPath photoFileName = createString("photoFileName");

    public final StringPath signType = createString("signType");

    public final StringPath userId = createString("userId");

    public final StringPath userName = createString("userName");

    public final StringPath userPwd = createString("userPwd");

    public QMemberEntity(String variable) {
        super(MemberEntity.class, forVariable(variable));
    }

    public QMemberEntity(Path<? extends MemberEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMemberEntity(PathMetadata metadata) {
        super(MemberEntity.class, metadata);
    }

}

