package com.example.ticketing.repository.recruit;

import com.example.ticketing.model.recruit.QRecruitmentPost;
import com.example.ticketing.model.recruit.RecruitmentPost;
import com.example.ticketing.model.recruit.RecruitmentStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class RecruitmentRepositoryImpl implements RecruitmentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<RecruitmentPost> findUrgentRecruitments(Pageable pageable) {
        QRecruitmentPost recruitmentPost = QRecruitmentPost.recruitmentPost;

        List<RecruitmentPost> content = queryFactory
                .selectFrom(recruitmentPost)
                .where(
                        recruitmentPost.status.eq(RecruitmentStatus.OPEN),
                        recruitmentPost.meetingTime.after(LocalDateTime.now()),
                        recruitmentPost.meetingTime.before(LocalDateTime.now().plusHours(24))
                )
                .orderBy(recruitmentPost.meetingTime.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(recruitmentPost)
                .where(
                        recruitmentPost.status.eq(RecruitmentStatus.OPEN),
                        recruitmentPost.meetingTime.after(LocalDateTime.now()),
                        recruitmentPost.meetingTime.before(LocalDateTime.now().plusHours(24))
                )
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<RecruitmentPost> findAlmostFullRecruitments(Pageable pageable) {
        QRecruitmentPost recruitmentPost = QRecruitmentPost.recruitmentPost;

        List<RecruitmentPost> content = queryFactory
                .selectFrom(recruitmentPost)
                .where(
                        recruitmentPost.status.eq(RecruitmentStatus.OPEN),
                        recruitmentPost.maxParticipants.subtract(recruitmentPost.currentParticipants).loe(2)
                )
                .orderBy(
                        recruitmentPost.maxParticipants.subtract(recruitmentPost.currentParticipants).asc(),
                        recruitmentPost.meetingTime.asc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(recruitmentPost)
                .where(
                        recruitmentPost.status.eq(RecruitmentStatus.OPEN),
                        recruitmentPost.meetingTime.after(LocalDateTime.now()),
                        recruitmentPost.meetingTime.before(LocalDateTime.now().plusHours(24))
                )
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }
}
