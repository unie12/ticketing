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

    public final ListPath<com.example.ticketing.model.chat.ChatRoomParticipant, com.example.ticketing.model.chat.QChatRoomParticipant> chatRoomParticipants = this.<com.example.ticketing.model.chat.ChatRoomParticipant, com.example.ticketing.model.chat.QChatRoomParticipant>createList("chatRoomParticipants", com.example.ticketing.model.chat.ChatRoomParticipant.class, com.example.ticketing.model.chat.QChatRoomParticipant.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final BooleanPath emailVerified = createBoolean("emailVerified");

    public final ListPath<com.example.ticketing.model.favorite.Favorite, com.example.ticketing.model.favorite.QFavorite> favorites = this.<com.example.ticketing.model.favorite.Favorite, com.example.ticketing.model.favorite.QFavorite>createList("favorites", com.example.ticketing.model.favorite.Favorite.class, com.example.ticketing.model.favorite.QFavorite.class, PathInits.DIRECT2);

    public final ListPath<com.example.ticketing.model.heart.Heart, com.example.ticketing.model.heart.QHeart> hearts = this.<com.example.ticketing.model.heart.Heart, com.example.ticketing.model.heart.QHeart>createList("hearts", com.example.ticketing.model.heart.Heart.class, com.example.ticketing.model.heart.QHeart.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imgUrl = createString("imgUrl");

    public final ListPath<com.example.ticketing.model.chat.ChatMessage, com.example.ticketing.model.chat.QChatMessage> messages = this.<com.example.ticketing.model.chat.ChatMessage, com.example.ticketing.model.chat.QChatMessage>createList("messages", com.example.ticketing.model.chat.ChatMessage.class, com.example.ticketing.model.chat.QChatMessage.class, PathInits.DIRECT2);

    public final ListPath<com.example.ticketing.model.recruit.Participant, com.example.ticketing.model.recruit.QParticipant> participants = this.<com.example.ticketing.model.recruit.Participant, com.example.ticketing.model.recruit.QParticipant>createList("participants", com.example.ticketing.model.recruit.Participant.class, com.example.ticketing.model.recruit.QParticipant.class, PathInits.DIRECT2);

    public final StringPath password = createString("password");

    public final EnumPath<com.example.ticketing.model.auth.AuthProvider> provider = createEnum("provider", com.example.ticketing.model.auth.AuthProvider.class);

    public final ListPath<com.example.ticketing.model.recruit.RecruitmentPost, com.example.ticketing.model.recruit.QRecruitmentPost> recruitmentPosts = this.<com.example.ticketing.model.recruit.RecruitmentPost, com.example.ticketing.model.recruit.QRecruitmentPost>createList("recruitmentPosts", com.example.ticketing.model.recruit.RecruitmentPost.class, com.example.ticketing.model.recruit.QRecruitmentPost.class, PathInits.DIRECT2);

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

