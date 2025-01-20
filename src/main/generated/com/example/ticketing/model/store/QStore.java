package com.example.ticketing.model.store;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStore is a Querydsl query type for Store
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStore extends EntityPathBase<Store> {

    private static final long serialVersionUID = -219320538L;

    public static final QStore store = new QStore("store");

    public final StringPath addressName = createString("addressName");

    public final StringPath categoryGroupCode = createString("categoryGroupCode");

    public final StringPath categoryGroupName = createString("categoryGroupName");

    public final StringPath categoryName = createString("categoryName");

    public final NumberPath<Integer> favoriteCount = createNumber("favoriteCount", Integer.class);

    public final ListPath<com.example.ticketing.model.favorite.Favorite, com.example.ticketing.model.favorite.QFavorite> favorites = this.<com.example.ticketing.model.favorite.Favorite, com.example.ticketing.model.favorite.QFavorite>createList("favorites", com.example.ticketing.model.favorite.Favorite.class, com.example.ticketing.model.favorite.QFavorite.class, PathInits.DIRECT2);

    public final StringPath id = createString("id");

    public final StringPath phone = createString("phone");

    public final StringPath placeName = createString("placeName");

    public final StringPath placeUrl = createString("placeUrl");

    public final NumberPath<Integer> reviewCount = createNumber("reviewCount", Integer.class);

    public final StringPath roadAddressName = createString("roadAddressName");

    public final ListPath<StoreCategoryMapping, QStoreCategoryMapping> storeCategoryMappings = this.<StoreCategoryMapping, QStoreCategoryMapping>createList("storeCategoryMappings", StoreCategoryMapping.class, QStoreCategoryMapping.class, PathInits.DIRECT2);

    public final NumberPath<Double> x = createNumber("x", Double.class);

    public final NumberPath<Double> y = createNumber("y", Double.class);

    public QStore(String variable) {
        super(Store.class, forVariable(variable));
    }

    public QStore(Path<? extends Store> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStore(PathMetadata metadata) {
        super(Store.class, metadata);
    }

}

