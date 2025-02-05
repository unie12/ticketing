package com.example.ticketing.repository.recruit;

import com.example.ticketing.model.recruit.RecruitmentPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecruitmentRepositoryCustom {
    Page<RecruitmentPost> findUrgentRecruitments(Pageable pageable);
    Page<RecruitmentPost> findAlmostFullRecruitments(Pageable pageable);
}
