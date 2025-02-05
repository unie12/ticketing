package com.example.ticketing.repository.recruit;

import com.example.ticketing.model.recruit.RecruitmentPost;
import com.example.ticketing.model.recruit.RecruitmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitmentRepository extends JpaRepository<RecruitmentPost, Long>, RecruitmentRepositoryCustom {
    Page<RecruitmentPost> findByStoreIdAndStatus(String storeId, RecruitmentStatus status, Pageable pageable);

    Page<RecruitmentPost> findByAuthorId(Long userId, Pageable pageable);

    @Query("SELECT r FROM RecruitmentPost r JOIN r.participants p WHERE p.user.id = :userId")
    Page<RecruitmentPost> findByParticipantUserId(@Param("userId") Long userId, Pageable pageable);
}
