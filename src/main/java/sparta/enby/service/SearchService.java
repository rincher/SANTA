package sparta.enby.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sparta.enby.dto.BoardResponseDto;
import sparta.enby.dto.ReviewResponseDto;
import sparta.enby.model.Board;
import sparta.enby.model.Review;
import sparta.enby.repository.BoardRepository;
import sparta.enby.repository.ReviewRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final BoardRepository boardRepository;
    private final ReviewRepository reviewRepository;

    public ResponseEntity<Map<String,Object>> boardSearch(String keyword) {
        List<Board> searchBoardTitle = boardRepository.findAllByTitleContaining(keyword ,Sort.by(Sort.Direction.DESC,"meetTime"));
        List<Board> searchBoardContent = boardRepository.findAllByContentsContaining(keyword, Sort.by(Sort.Direction.DESC,"meetTime"));
        List<BoardResponseDto> titleList =
                searchBoardTitle.stream().map(
                        board -> new BoardResponseDto(
                                board.getId(),
                                board.getTitle(),
                                board.getBoard_imgUrl(),
                                board.getPeople_current(),
                                board.getPeople_max(),
                                board.getContents(),
                                board.getLocation(),
                                board.getMeetTime(),
                                board.getDeadlineStatus()
                        )
                ).collect(Collectors.toList());
        List<BoardResponseDto> contentList =
                searchBoardContent.stream().map(
                        board -> new BoardResponseDto(
                                board.getId(),
                                board.getTitle(),
                                board.getBoard_imgUrl(),
                                board.getPeople_current(),
                                board.getPeople_max(),
                                board.getContents(),
                                board.getLocation(),
                                board.getMeetTime(),
                                board.getDeadlineStatus()
                        )
                ).collect(Collectors.toList());

        Set<BoardResponseDto> toSet = Stream.concat(titleList.stream(),contentList.stream()).collect(Collectors.toSet());
        List<BoardResponseDto> toList = new ArrayList<>(toSet);
        Map<String, Object> map = new HashMap<>();
        map.put("board",toList);
        return ResponseEntity.ok().body(map);
    }

    public ResponseEntity <Map<String,Object>> reviewSearch(String keyword){
        List<Review> reviewTitle = reviewRepository.findAllByTitleContaining(keyword);
        List<Review> reviewContent = reviewRepository.findAllByContentsContaining(keyword);
        List<ReviewResponseDto> searchReviewTitle = reviewTitle.stream().map(
                review -> new ReviewResponseDto(
                        review.getId(),
                        review.getTitle(),
                        review.getReview_imgUrl(),
                        review.getContents(),
                        review.getBoard().getId(),
                        review.getCreatedAt(),
                        review.getAccount().getNickname(),
                        review.getAccount().getProfile_img()
                )
        ).collect(Collectors.toList());
        List<ReviewResponseDto> searchReviewContent = reviewContent.stream().map(
                review -> new ReviewResponseDto(
                        review.getId(),
                        review.getTitle(),
                        review.getReview_imgUrl(),
                        review.getContents(),
                        review.getBoard().getId(),
                        review.getCreatedAt(),
                        review.getAccount().getNickname(),
                        review.getAccount().getProfile_img()
                )
        ).collect(Collectors.toList());
        Set<ReviewResponseDto> toset = Stream.concat(searchReviewTitle.stream(),searchReviewContent.stream()).collect(Collectors.toSet());
        Map<String, Object> map = new HashMap<>();
        map.put("review",toset);
        return ResponseEntity.ok().body(map);
    }
}
