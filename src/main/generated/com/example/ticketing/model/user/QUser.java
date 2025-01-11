package com.example.ticketing.model.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 760972896L;

    public static final QUser user = new QUser("user");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final BooleanPath emailVerified = createBoolean("emailVerified");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imgUrl = createString("imgUrl");

    public final StringPath password = createString("password");

    public final EnumPath<com.example.ticketing.model.auth.AuthProvider> provider = createEnum("provider", com.example.ticketing.model.auth.AuthProvider.class);

    public final ListPath<com.example.ticketing.model.event.Reservation, com.example.ticketing.model.event.QReservation> reservations = this.<com.example.ticketing.model.event.Reservation, com.example.ticketing.model.event.QReservation>createList("reservations", com.example.ticketing.model.event.Reservation.class, com.example.ticketing.model.event.QReservation.class, PathInits.DIRECT2);

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public final ListPath<UserCoupon, QUserCoupon> userCoupons = this.<UserCoupon, QUserCoupon>createList("userCoupons", UserCoupon.class, QUserCoupon.class, PathInits.DIRECT2);

    public final StringPath username = createString("username");

    public final StringPath verificationToken = createString("verificationToken");

    public final DateTimePath<java.time.LocalDateTime> verificationTokenExpiry = createDateTime("verificationTokenExpiry", java.time.LocalDateTime.class);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

