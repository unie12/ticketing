package com.example.ticketing.repository.review;

import com.example.ticketing.model.review.VisitInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitInfoRepository extends JpaRepository<VisitInfo, Long> {
}
