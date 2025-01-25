package com.example.ticketing.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SearchActivityEvent extends UserActivityEvent {
    private String query;
    private String categoryGroupCode;
//    private Map<String, String> filters; 검색 시 필터 조건인데 이건 api 정보 제한 때문에 힘들듯?
}
