package com.example.ticketing.controller.recruit;

import com.example.ticketing.model.recruit.*;
import com.example.ticketing.security.JwtTokenProvider;
import com.example.ticketing.service.recruit.RecruitmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store/{storeId}/recruitments")
@Tag(name = "Recruitment API", description = "밥친구 구인 관련 API")
public class RecruitmentController {
    private final RecruitmentService recruitmentService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 구인글 탈퇴
     */

    @PostMapping
    @Operation(summary = "구인글 작성", description = "특정 가게에 대한 밥친구 구인글을 작성합니다.")
    public ResponseEntity<RecruitmentResponseDTO> createRecruitment(
            @PathVariable String storeId,
            @Valid @RequestBody RecruitmentRequest request,
            @RequestHeader("Authorization") String token
    ) {
       Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
       return ResponseEntity.status(HttpStatus.CREATED)
               .body(recruitmentService.createRecruitment(storeId, userId, request));
    }

    @GetMapping
    @Operation(summary = "구인글 목록 조회", description = "특정 가게의 밥친구 구인글 목록을 조회합니다.")
    public ResponseEntity<Page<RecruitmentResponseDTO>> getRecruitments(
            @PathVariable String storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
//            @RequestParam(required = false) RecruitmentStatus status
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(recruitmentService.getRecruitments(storeId, pageRequest));
    }

    @GetMapping("/{recruitmentId}")
    @Operation(summary = "구인글 상세 조회", description = "특정 구인글의 상세 정보를 조회합니다.")
    public ResponseEntity<RecruitmentDetailDTO> getRecruitment(
            @PathVariable String storeId,
            @PathVariable Long recruitmentId
    ) {
        return ResponseEntity.ok(recruitmentService.getRecruitment(recruitmentId));
    }

    @PutMapping("/{recruitmentId}")
    @Operation(summary = "구인글 수정", description = "작성자만 구인글을 수정할 수 있습니다.")
    public ResponseEntity<RecruitmentResponseDTO> updateRecruitment(
            @PathVariable String storeId,
            @PathVariable Long recruitmentId,
            @Valid @RequestBody RecruitmentRequest request,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        return ResponseEntity.ok(recruitmentService.updateRecruitment(recruitmentId, userId, request));
    }

    @DeleteMapping("/{recruitmentId}")
    @Operation(summary = "구인글 삭제", description = "작성자만 구인글을 삭제할 수 있습니다.")
    public ResponseEntity<Void> deleteRecruitment(
            @PathVariable String storeId,
            @PathVariable Long recruitmentId,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        recruitmentService.deleteRecruitment(recruitmentId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{recruitmentId}/join")
    @Operation(summary = "구인글 참여 신청", description = "구인글에 참여 신청을 합니다.")
    public ResponseEntity<ParticipantResponseDTO> joinRecruitment(
            @PathVariable String storeId,
            @PathVariable Long recruitmentId,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        return ResponseEntity.ok(recruitmentService.joinRecruitment(recruitmentId, userId));
    }

    @PostMapping("/{recruitmentId}/close")
    @Operation(summary = "구인글 마감", description = "작성자만 구인글을 마감할 수 있습니다.")
    public ResponseEntity<RecruitmentResponseDTO> closeRecruitment(
            @PathVariable String storeId,
            @PathVariable Long recruitmentId,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        return ResponseEntity.ok(recruitmentService.closeRecruitment(recruitmentId, userId));
    }

    @GetMapping("/my")
    @Operation(summary = "내 구인글 목록", description = "자신이 작성한 구인글 목록을 조회합니다.")
    public ResponseEntity<Page<RecruitmentResponseDTO>> getMyRecruitments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(recruitmentService.getMyRecruitments(userId, pageRequest));
    }

    @GetMapping("/joined")
    @Operation(summary = "참여 구인글 목록", description = "자신이 참여한 구인글 목록을 조회합니다.")
    public ResponseEntity<Page<RecruitmentResponseDTO>> getJoinedRecruitments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(recruitmentService.getJoinedRecruitments(userId, pageRequest));
    }

    @GetMapping("/urgent")
    @Operation(summary = "마감 임박 구인글", description = "24시간 이내 마감되는 구인글을 조회합니다.")
    public ResponseEntity<Page<RecruitmentResponseDTO>> getUrgentRecruitments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(recruitmentService.getUrgentRecruitments(pageRequest));
    }

    @GetMapping("/almost-full")
    @Operation(summary = "마감 임박 구인글", description = "인원이 거의 찬 구인글을 조회합니다.")
    public ResponseEntity<Page<RecruitmentResponseDTO>> getAlmostFullRecruitments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(recruitmentService.getAlmostFullRecruitments(pageRequest));
    }


}
