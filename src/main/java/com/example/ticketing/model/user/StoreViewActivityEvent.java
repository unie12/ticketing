package com.example.ticketing.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StoreViewActivityEvent extends UserActivityEvent {
    private String categoryGroupCode;
    private String categoryGroupName;
    private Double viewDuration;
//    private List<Long> categoryIds;
//    private String accessPath; // 검색이나 추천 직접 접근 등??? 아직은 설정 x
}
