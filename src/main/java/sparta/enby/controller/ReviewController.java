package sparta.enby.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sparta.enby.dto.ReviewRequestDto;
import sparta.enby.dto.ReviewResponseDto;
import sparta.enby.security.UserDetailsImpl;
import sparta.enby.service.ReviewService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    //후기 리스트
    @GetMapping("/main/review")
    public ResponseEntity<List<ReviewResponseDto>> getReviewList(){
        return reviewService.getReviewList();
    }

    //후기 페이지
    @GetMapping("/board/mating/review")
    public Page<ReviewResponseDto> getReviewPage(@RequestParam("page") int page, @RequestParam("size") int size){
        return reviewService.getReviewPage(page, size);
    }

    //후기 상세 페이지 가져오기
    @GetMapping("/board/mating/review/{review_id}")
    public List<ReviewResponseDto> getDetailReview(@PathVariable Long review_id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return reviewService.getDetailReview(review_id, userDetails);
    }

    //review 작성
    @PostMapping("/board/mating/{board_id}/review")
    public ResponseEntity<String> writeReview(@PathVariable Long board_id, @ModelAttribute ReviewRequestDto reviewRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return reviewService.writeReview(board_id, reviewRequestDto, userDetails);
    }

    //후기 수정
    @PutMapping("/board/mating/{board_id}/review/{review_id}")
    public ResponseEntity <String> editReview(@ModelAttribute ReviewRequestDto reviewRequestDto, @PathVariable Long board_id, @PathVariable Long review_id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return reviewService.editReview(reviewRequestDto,board_id, review_id, userDetails);
    }

    //후기 삭제
    @DeleteMapping("/board/mating/review/{review_id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long review_id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return reviewService.deleteReview(review_id,userDetails);
    }
}
