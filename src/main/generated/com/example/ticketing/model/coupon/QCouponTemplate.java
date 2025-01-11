package com.example.ticketing.model.coupon;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCouponTemplate is a Querydsl query type for CouponTemplate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCouponTemplate extends EntityPathBase<CouponTemplate> {

    private static final long serialVersionUID = 1366357210L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCouponTemplate couponTemplate = new QCouponTemplate("couponTemplate");

    public final QCouponEvent couponEvent;

    public final NumberPath<Integer> discountAmount = createNumber("discountAmount", Integer.class);

    public final EnumPath<DiscountType> discountType = createEnum("discountType", DiscountType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> remaining = createNumber("remaining", Integer.class);

    public final NumberPath<Integer> totalQuantity = createNumber("totalQuantity", Integer.class);

    public final ListPath<com.example.ticketing.model.user.UserCoupon, com.example.ticketing.model.user.QUserCoupon> userCoupons = this.<com.example.ticketing.model.user.UserCoupon, com.example.ticketing.model.user.QUserCoupon>createList("userCoupons", com.example.ticketing.model.user.UserCoupon.class, com.example.ticketing.model.user.QUserCoupon.class, PathInits.DIRECT2);

    public final NumberPath<Integer> weight = createNumber("weight", Integer.class);

    public QCouponTemplate(String variable) {
        this(CouponTemplate.class, forVariable(variable), INITS);
    }

    public QCouponTemplate(Path<? extends CouponTemplate> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCouponTemplate(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCouponTemplate(PathMetadata metadata, PathInits inits) {
        this(CouponTemplate.class, metadata, inits);
    }

    public QCouponTemplate(Class<? extends CouponTemplate> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.couponEvent = inits.isInitialized("couponEvent") ? new QCouponEvent(forProperty("couponEvent"), inits.get("couponEvent")) : null;
    }

}

