package com.example.ticketing.service.recruit;

import com.example.ticketing.exception.ErrorCode;
import com.example.ticketing.exception.RecruitmentException;
import com.example.ticketing.model.recruit.*;
import com.example.ticketing.model.store.Store;
import com.example.ticketing.model.user.User;
import com.example.ticketing.repository.recruit.RecruitmentRepository;
import com.example.ticketing.service.store.StoreService;
import com.example.ticketing.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentServiceImpl implements RecruitmentService{
    private final StoreService storeService;
    private final UserService userService;

    private final RecruitmentRepository recruitmentRepository;

    @Override
    @Transactional
    public RecruitmentResponseDTO createRecruitment(String storeId, Long userId, RecruitmentRequest request) {
        Store store = storeService.findStoreById(storeId);
        User user = userService.findUserById(userId);

        RecruitmentPost recruitmentPost = RecruitmentPost.builder()
                .author(user)
                .store(store)
                .title(request.getTitle())
                .content(request.getContent())
                .maxParticipants(request.getMaxParticipants())
                .meetingTime(request.getMeetingTime())
                .build();
        recruitmentPost.incrementParticipants();

        return RecruitmentResponseDTO.from(recruitmentRepository.save(recruitmentPost));
    }

    @Override
    public RecruitmentDetailDTO getRecruitment(Long recruitmentId) {
        RecruitmentPost recruitmentPost = findRecruitmentPostByRecruitmentPostId(recruitmentId);

        return RecruitmentDetailDTO.from(recruitmentPost);
    }

    @Override
    @Transactional
    public RecruitmentResponseDTO updateRecruitment(Long recruitmentId, Long userId, RecruitmentRequest request) {
        RecruitmentPost recruitmentPost = findRecruitmentPostByRecruitmentPostId(recruitmentId);
        User user = userService.findUserById(userId);

        if (!recruitmentPost.isAuthor(user)) {
            throw new RecruitmentException(ErrorCode.RECRUITMENTPOST_NOT_AUTHOR);
        }

        if (recruitmentPost.getStatus() != RecruitmentStatus.OPEN) {
            throw  new RecruitmentException(ErrorCode.RECRUITMENTPOST_ALREADY_CLOSED);
        }

        if (request.getMaxParticipants() < recruitmentPost.getCurrentParticipants()) {
            throw new RecruitmentException(ErrorCode.RECRUITMENTPOST_INVALID_MAX_PARTICIPANTS);
        }

        if (request.getMeetingTime().isBefore(LocalDateTime.now())) {
            throw new RecruitmentException(ErrorCode.RECRUITMENTPOST_INVALID_MEETING_TIME);
        }

        recruitmentPost.update(request);
        return RecruitmentResponseDTO.from(recruitmentPost);
    }

    @Override
    @Transactional
    public void deleteRecruitment(Long recruitmentId, Long userId) {
        RecruitmentPost recruitmentPost = findRecruitmentPostByRecruitmentPostId(recruitmentId);
        User user = userService.findUserById(userId);

        if (!recruitmentPost.isAuthor(user)) {
            throw new RecruitmentException(ErrorCode.RECRUITMENTPOST_NOT_AUTHOR);
        }

        recruitmentRepository.delete(recruitmentPost);
    }

    @Override
    @Transactional
    public ParticipantResponseDTO joinRecruitment(Long recruitmentId, Long userId) {
        RecruitmentPost recruitmentPost = findRecruitmentPostByRecruitmentPostId(recruitmentId);
        User user = userService.findUserById(userId);

        if (user.hasJoinedRecruitment(recruitmentPost)) {
            throw new RecruitmentException(ErrorCode.RECRUITMENTPOST_ALREADY_JOINED);
        }

        if (recruitmentPost.isAuthor(user)) {
            throw new RecruitmentException(ErrorCode.RECRUITMENTPOST_AUTHOR_CANNOT_JOIN);
        }

        if (!recruitmentPost.canJoin()) {
            throw new RecruitmentException(ErrorCode.RECRUITMENTPOST_JOIN_NOT_ALLOWED);
        }

        Participant participant = new Participant(user, recruitmentPost);
        recruitmentPost.incrementParticipants();

        return ParticipantResponseDTO.from(participant);
    }

    @Override
    @Transactional
    public RecruitmentResponseDTO closeRecruitment(Long recruitmentId, Long userId) {
        RecruitmentPost recruitmentPost = findRecruitmentPostByRecruitmentPostId(recruitmentId);
        User user = userService.findUserById(userId);

        if (!recruitmentPost.isAuthor(user)) {
            throw new RecruitmentException(ErrorCode.RECRUITMENTPOST_NOT_AUTHOR);
        }

        if (!recruitmentPost.getStatus().equals(RecruitmentStatus.OPEN)) {
            throw new RecruitmentException(ErrorCode.RECRUITMENTPOST_ALREADY_CLOSED);
        }
        recruitmentPost.close();
        return RecruitmentResponseDTO.from(recruitmentPost);
    }

    @Override
    public Page<RecruitmentResponseDTO> getRecruitments(String storeId, RecruitmentStatus status, PageRequest pageRequest) {
        return recruitmentRepository.findByStoreIdAndStatus(storeId, status, pageRequest)
                .map(RecruitmentResponseDTO::from);
    }

    @Override
    public Page<RecruitmentResponseDTO> getMyRecruitments(Long userId, PageRequest pageRequest) {
        return recruitmentRepository.findByAuthorId(userId, pageRequest)
                .map(RecruitmentResponseDTO::from);
    }

    @Override
    public Page<RecruitmentResponseDTO> getJoinedRecruitments(Long userId, PageRequest pageRequest) {
        return recruitmentRepository.findByParticipantUserId(userId, pageRequest)
                .map(RecruitmentResponseDTO::from);
    }

    @Override
    public Page<RecruitmentResponseDTO> getUrgentRecruitments(PageRequest pageRequest) {
        return recruitmentRepository.findUrgentRecruitments(pageRequest)
                .map(RecruitmentResponseDTO::from);
    }

    @Override
    public Page<RecruitmentResponseDTO> getAlmostFullRecruitments(PageRequest pageRequest) {
        return recruitmentRepository.findAlmostFullRecruitments(pageRequest)
                .map(RecruitmentResponseDTO::from);
    }

    private RecruitmentPost findRecruitmentPostByRecruitmentPostId(Long recruitmentId) {
        return recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(ErrorCode.RECRUITMENTPOST_NOT_FOUND));
    }
}
