package com.example.ticketing.model.recruit;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthorDTO {
    private Long id;
    private String username;
    private String imgUrl;
}
