package com.example.ticketing.service.recruit;

import com.example.ticketing.model.recruit.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface RecruitmentService {
    RecruitmentResponseDTO createRecruitment(String storeId, Long userId, RecruitmentRequest request);


    RecruitmentDetailDTO getRecruitment(Long recruitmentId);

    RecruitmentResponseDTO updateRecruitment(Long recruitmentId, Long userId, RecruitmentRequest request);

    void deleteRecruitment(Long recruitmentId, Long userId);

    ParticipantResponseDTO joinRecruitment(Long recruitmentId, Long userId);

    RecruitmentResponseDTO closeRecruitment(Long recruitmentId, Long userId);

    Page<RecruitmentResponseDTO> getRecruitments(String storeId, RecruitmentStatus status, PageRequest pageRequest);

    Page<RecruitmentResponseDTO> getMyRecruitments(Long userId, PageRequest pageRequest);

    Page<RecruitmentResponseDTO> getJoinedRecruitments(Long userId, PageRequest pageRequest);
}
