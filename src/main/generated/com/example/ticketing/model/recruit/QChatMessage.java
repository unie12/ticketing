package com.example.ticketing.model.recruit;

import com.example.ticketing.model.chat.ChatMessage;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;

import javax.annotation.processing.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QChatMessage is a Querydsl query type for ChatMessage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatMessage extends EntityPathBase<ChatMessage> {

    private static final long serialVersionUID = -719608207L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatMessage chatMessage = new QChatMessage("chatMessage");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath messageContent = createString("messageContent");

    public final QRecruitmentPost recruitmentPost;

    public final com.example.ticketing.model.user.QUser sender;

    public final DateTimePath<java.time.LocalDateTime> timestamp = createDateTime("timestamp", java.time.LocalDateTime.class);

    public QChatMessage(String variable) {
        this(ChatMessage.class, forVariable(variable), INITS);
    }

    public QChatMessage(Path<? extends ChatMessage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatMessage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatMessage(PathMetadata metadata, PathInits inits) {
        this(ChatMessage.class, metadata, inits);
    }

    public QChatMessage(Class<? extends ChatMessage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.recruitmentPost = inits.isInitialized("recruitmentPost") ? new QRecruitmentPost(forProperty("recruitmentPost"), inits.get("recruitmentPost")) : null;
        this.sender = inits.isInitialized("sender") ? new com.example.ticketing.model.user.QUser(forProperty("sender")) : null;
    }

}

