package com.example.ticketing.model.store;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStoreCategoryMapping is a Querydsl query type for StoreCategoryMapping
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStoreCategoryMapping extends EntityPathBase<StoreCategoryMapping> {

    private static final long serialVersionUID = -1356770838L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStoreCategoryMapping storeCategoryMapping = new QStoreCategoryMapping("storeCategoryMapping");

    public final QCategory category;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QStore store;

    public QStoreCategoryMapping(String variable) {
        this(StoreCategoryMapping.class, forVariable(variable), INITS);
    }

    public QStoreCategoryMapping(Path<? extends StoreCategoryMapping> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStoreCategoryMapping(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStoreCategoryMapping(PathMetadata metadata, PathInits inits) {
        this(StoreCategoryMapping.class, metadata, inits);
    }

    public QStoreCategoryMapping(Class<? extends StoreCategoryMapping> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category"), inits.get("category")) : null;
        this.store = inits.isInitialized("store") ? new QStore(forProperty("store")) : null;
    }

}

