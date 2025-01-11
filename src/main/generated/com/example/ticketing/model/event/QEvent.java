package com.example.ticketing.model.event;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEvent is a Querydsl query type for Event
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEvent extends EntityPathBase<Event> {

    private static final long serialVersionUID = 1703310296L;

    public static final QEvent event = new QEvent("event");

    public final ListPath<com.example.ticketing.model.coupon.CouponEvent, com.example.ticketing.model.coupon.QCouponEvent> couponEvents = this.<com.example.ticketing.model.coupon.CouponEvent, com.example.ticketing.model.coupon.QCouponEvent>createList("couponEvents", com.example.ticketing.model.coupon.CouponEvent.class, com.example.ticketing.model.coupon.QCouponEvent.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> endTime = createDateTime("endTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> remainingSeats = createNumber("remainingSeats", Integer.class);

    public final ListPath<Reservation, QReservation> reservations = this.<Reservation, QReservation>createList("reservations", Reservation.class, QReservation.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    public final NumberPath<Integer> totalSeats = createNumber("totalSeats", Integer.class);

    public QEvent(String variable) {
        super(Event.class, forVariable(variable));
    }

    public QEvent(Path<? extends Event> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEvent(PathMetadata metadata) {
        super(Event.class, metadata);
    }

}

