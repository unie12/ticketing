package com.example.ticketing.model.recruit;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecruitmentPost is a Querydsl query type for RecruitmentPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecruitmentPost extends EntityPathBase<RecruitmentPost> {

    private static final long serialVersionUID = 2136539198L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecruitmentPost recruitmentPost = new QRecruitmentPost("recruitmentPost");

    public final com.example.ticketing.model.user.QUser author;

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> currentParticipants = createNumber("currentParticipants", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> lastModifiedAt = createDateTime("lastModifiedAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> maxParticipants = createNumber("maxParticipants", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> meetingTime = createDateTime("meetingTime", java.time.LocalDateTime.class);

    public final ListPath<Participant, QParticipant> participants = this.<Participant, QParticipant>createList("participants", Participant.class, QParticipant.class, PathInits.DIRECT2);

    public final EnumPath<RecruitmentStatus> status = createEnum("status", RecruitmentStatus.class);

    public final com.example.ticketing.model.store.QStore store;

    public final StringPath title = createString("title");

    public QRecruitmentPost(String variable) {
        this(RecruitmentPost.class, forVariable(variable), INITS);
    }

    public QRecruitmentPost(Path<? extends RecruitmentPost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecruitmentPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecruitmentPost(PathMetadata metadata, PathInits inits) {
        this(RecruitmentPost.class, metadata, inits);
    }

    public QRecruitmentPost(Class<? extends RecruitmentPost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.example.ticketing.model.user.QUser(forProperty("author")) : null;
        this.store = inits.isInitialized("store") ? new com.example.ticketing.model.store.QStore(forProperty("store")) : null;
    }

}

