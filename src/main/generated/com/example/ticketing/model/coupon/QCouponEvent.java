package com.example.ticketing.model.coupon;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCouponEvent is a Querydsl query type for CouponEvent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCouponEvent extends EntityPathBase<CouponEvent> {

    private static final long serialVersionUID = -644340166L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCouponEvent couponEvent = new QCouponEvent("couponEvent");

    public final ListPath<CouponTemplate, QCouponTemplate> couponTemplates = this.<CouponTemplate, QCouponTemplate>createList("couponTemplates", CouponTemplate.class, QCouponTemplate.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> endTime = createDateTime("endTime", java.time.LocalDateTime.class);

    public final com.example.ticketing.model.event.QEvent event;

    public final StringPath eventName = createString("eventName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> validityEndTime = createDateTime("validityEndTime", java.time.LocalDateTime.class);

    public QCouponEvent(String variable) {
        this(CouponEvent.class, forVariable(variable), INITS);
    }

    public QCouponEvent(Path<? extends CouponEvent> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCouponEvent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCouponEvent(PathMetadata metadata, PathInits inits) {
        this(CouponEvent.class, metadata, inits);
    }

    public QCouponEvent(Class<? extends CouponEvent> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.event = inits.isInitialized("event") ? new com.example.ticketing.model.event.QEvent(forProperty("event")) : null;
    }

}

