package com.example.ticketing.model.review;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVisitInfo is a Querydsl query type for VisitInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVisitInfo extends EntityPathBase<VisitInfo> {

    private static final long serialVersionUID = -1030949135L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVisitInfo visitInfo = new QVisitInfo("visitInfo");

    public final EnumPath<Crowdedness> crowdedness = createEnum("crowdedness", Crowdedness.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QReview review;

    public final com.example.ticketing.model.store.QStore store;

    public final com.example.ticketing.model.user.QUser user;

    public final DateTimePath<java.time.LocalDateTime> visitDateTime = createDateTime("visitDateTime", java.time.LocalDateTime.class);

    public QVisitInfo(String variable) {
        this(VisitInfo.class, forVariable(variable), INITS);
    }

    public QVisitInfo(Path<? extends VisitInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVisitInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVisitInfo(PathMetadata metadata, PathInits inits) {
        this(VisitInfo.class, metadata, inits);
    }

    public QVisitInfo(Class<? extends VisitInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.review = inits.isInitialized("review") ? new QReview(forProperty("review"), inits.get("review")) : null;
        this.store = inits.isInitialized("store") ? new com.example.ticketing.model.store.QStore(forProperty("store")) : null;
        this.user = inits.isInitialized("user") ? new com.example.ticketing.model.user.QUser(forProperty("user")) : null;
    }

}

