package sparta.enby.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sparta.enby.dto.*;
import sparta.enby.model.Account;
import sparta.enby.model.Board;
import sparta.enby.model.Registration;
import sparta.enby.model.Review;
import sparta.enby.repository.AccountRepository;
import sparta.enby.repository.BoardRepository;
import sparta.enby.repository.RegistrationRepository;
import sparta.enby.repository.ReviewRepository;
import sparta.enby.security.UserDetailsImpl;
import sparta.enby.uploader.FileUploaderService;
import sparta.enby.uploader.S3Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final FileUploaderService fileUploaderService;
    private final ReviewRepository reviewRepository;
    private final RegistrationRepository registrationRepository;


    public ResponseEntity<List<BoardResponseDto>> getBoardList() {
        // 모든 게시글들을 List로 받아서
        List<Board> boards = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        // List에 있는 객체들을 BoardResponseDto에 명시된 객체들에 mapping하기 위해서 stream을 통해서 목록들을 읽어온 다음 list로 형변환
        List<BoardResponseDto> toList = boards.stream().map(
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
        return ResponseEntity.ok().body(toList);
    }

    //게시글 상세 페이지
    public ResponseEntity getDetailBoard(Long board_id, UserDetailsImpl userDetails) {
        // 해당 게사글 id로 찾은 List들을 stream으로 읽어와서 BoardDetailResponseDto에 map
        List<Board> boards = boardRepository.findAllById(board_id);
        List<BoardDetailResponseDto> toList = boards.stream().map(
                board -> new BoardDetailResponseDto(
                        board.getId(),
                        board.getCreatedBy(),
                        board.getTitle(),
                        board.getContents(),
                        board.getMeetTime(),
                        board.getCreatedAt(),
                        board.getLocation(),
                        board.getBoard_imgUrl(),
                        board.getPeople_current(),
                        board.getPeople_max(),
                        board.getDeadlineStatus(),
                        //여기에 게시글에 달린 후기 리뷰를 ReviewResponseDto에 매핑
                        board.getReviews().stream().map(
                                review -> new ReviewResponseDto(
                                        review.getId(),
                                        review.getTitle(),
                                        review.getReview_imgUrl(),
                                        review.getContents(),
                                        review.getBoard().getId(),
                                        review.getAccount().getNickname(),
                                        review.getAccount().getProfile_img()
                                )
                        ).collect(Collectors.toList()),
                        // 게시글에 참여 신청한 내역을 RegistrationResponseDto에 매핑
                        board.getRegistrations().stream().map(
                                registration -> new RegistrationResponseDto(
                                        registration.getId(),
                                        registration.isAccepted(),
                                        registration.getContents(),
                                        registration.getAccount().getNickname(),
                                        registration.getAccount().getProfile_img(),
                                        registration.getKakao_id()
                                )
                        ).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("boards", toList);
        return ResponseEntity.ok().body(map);
    }

    //전체 게시글 목록
    public Page<BoardResponseDto> getBoard(int page, int size, UserDetailsImpl userDetails) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Board> boards = boardRepository.findAll(pageRequest);
        //        Page<Board> boards = boardRepository.findAllByMeetTimeBefore(now,pageRequest);
        if (page > boards.getTotalPages()) {
            return null;
        }
        if (boards.isEmpty()) {
            return null;
        }
        Page<BoardResponseDto> toMap = boards.map(board -> new BoardResponseDto(
                board.getId(),
                board.getBoard_imgUrl(),
                board.getContents(),
                board.getTitle(),
                board.getLocation(),
                board.getMeetTime(),
                board.getDeadlineStatus()
        ));
        return toMap;
    }

    // 게시글 쓰기
    @Transactional
    public Long writeBoard(BoardRequestDto boardRequestDto, UserDetailsImpl userDetails) throws IOException {
        String board_imgUrl = null;
        if (boardRequestDto.getBoardImg() == null || boardRequestDto.getBoardImg().isEmpty()) {
            board_imgUrl = "https://hanghae99-gitlog.s3.ap-northeast-2.amazonaws.com/default_image.png";
        } else {
            board_imgUrl = fileUploaderService.uploadImage(boardRequestDto.getBoardImg());
        }
        String time = boardRequestDto.getMeetTime();
        LocalDateTime meeting_time = LocalDateTime.parse(time, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Board board = Board.builder()
                .title(boardRequestDto.getTitle())
                .meetTime(meeting_time)
                .people_max(boardRequestDto.getPeople_max())
                .location(boardRequestDto.getLocation())
                .contents(boardRequestDto.getContents())
                .board_imgUrl(board_imgUrl)
                .deadlineStatus(false).build();
        Board newBoard = boardRepository.save(board);
        newBoard.addAccount(userDetails.getAccount());
        return newBoard.getId();

    }

    //게시글 수정
    @Transactional
    public ResponseEntity<String> editBoard(Long board_id, BoardRequestDto boardRequestDto, UserDetailsImpl userDetails) {
        Board board = boardRepository.findById(board_id).orElse(null);
        if (board == null) {
            return new ResponseEntity<>("없는 게시판입니다", HttpStatus.BAD_REQUEST);
        } else {
            String board_imgUrl = null;
            if (boardRequestDto.getBoardImg() == null || boardRequestDto.getBoardImg().isEmpty()) {
                board_imgUrl = board.getBoard_imgUrl();
            } else {
                if (!board.getBoard_imgUrl().equals("https://hanghae99-gitlog.s3.ap-northeast-2.amazonaws.com/default_image.png")) {
                    fileUploaderService.removeImage(board.getBoard_imgUrl());
                }
                board_imgUrl = fileUploaderService.uploadImage(boardRequestDto.getBoardImg());
            }

            String title = null;
            if (boardRequestDto.getTitle() == null || boardRequestDto.getTitle().isEmpty()) {
                title = board.getTitle();
            } else {
                title = boardRequestDto.getTitle();
            }

            LocalDateTime time = null;
            if (boardRequestDto.getMeetTime() == null || boardRequestDto.getMeetTime().isEmpty()) {
                time = board.getMeetTime();
            } else {
                time = LocalDateTime.parse(boardRequestDto.getMeetTime());
            }

            String contents = null;
            if (boardRequestDto.getContents() == null || boardRequestDto.getContents().isEmpty()) {
                contents = board.getContents();
            } else {
                contents = boardRequestDto.getContents();
            }

            String location = null;
            if (boardRequestDto.getLocation() == null || boardRequestDto.getLocation().isEmpty()) {
                location = board.getLocation();
            } else {
                location = boardRequestDto.getLocation();
            }
            int people_max = 0;

            if (boardRequestDto.getPeople_max() > 5) {
                people_max = 4;
            }
            if (boardRequestDto.getPeople_max() < 0) {
                people_max = 0;
            }
            if (boardRequestDto.getPeople_max() == 0) {
                people_max = board.getPeople_max();
            }
            else{
                people_max = boardRequestDto.getPeople_max();
            }
            Boolean deadlineStatus = false;
            if (boardRequestDto.getDeadlineStatus() == null) {
                deadlineStatus = board.getDeadlineStatus();
            } else {
                deadlineStatus = boardRequestDto.getDeadlineStatus();
            }

            board.update(board_imgUrl, title, contents, time, location, people_max, deadlineStatus);

            return new ResponseEntity<>("성공적으로 수정하였습니다", HttpStatus.OK);
        }
    }

    //게시글 삭제
    @Transactional
    public ResponseEntity<String> deleteBoard(Long board_id, Account account) {
        Board board = boardRepository.findById(board_id).orElse(null);
        if (board == null) {
            return new ResponseEntity<>("없는 게시판입니다", HttpStatus.BAD_REQUEST);
        }
        if (!board.getAccount().getNickname().equals(account.getNickname())) {
            return new ResponseEntity<>("없는 사용자이거나 다른 사용자의 게시글입니다", HttpStatus.BAD_REQUEST);
        }
        if (!board.getReviews().isEmpty()) {
            if (reviewRepository.existsByBoard(board)) {
                List<Review> reviews = reviewRepository.findAllByBoard(board);
                for (Review review : reviews) {
                    if (!review.getReview_imgUrl().equals("https://hanghae99-gitlog.s3.ap-northeast-2.amazonaws.com/default_image.png")) {
                        fileUploaderService.removeImage(review.getReview_imgUrl());
                    }
                    reviewRepository.deleteAllByBoard(board);
                }
            }
        }
        if (!board.getRegistrations().isEmpty()) {
            if (registrationRepository.existsByBoardId(board_id)) {
                registrationRepository.deleteAllByBoard(board);
            }
        }
        board.deleteBoard(board);
        boardRepository.deleteById(board_id);
        return new ResponseEntity<>("성공적으로 삭제 하였습니다", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> clickFinish(Long board_id, ChangeDeadlineRequestDto changeDeadlineRequestDto, Account account) {
        Board board = boardRepository.findById(board_id).orElse(null);
        if (board == null) {
            return ResponseEntity.badRequest().body("해당 아이디의 게시글이 없습니다");
        }
        Boolean b = changeDeadlineRequestDto.getDeadlineStatus();
        board.changeDeadlineStatus(b);
        return ResponseEntity.ok().body("성공적으로 마감상태가 변경되었습니다");
    }

    public ResponseEntity<List<AttendedBoardDto>> getBoardWithoutReview(UserDetailsImpl userDetails) {
        List<Board> boardLists = new ArrayList<>();
        List<Board> boardwithnoreview = new ArrayList<>();
        List<AttendedBoardDto> attendedBoardList = new ArrayList<>();
        List<Review> reviewLists = new ArrayList<>();
        List<Registration> registrations = registrationRepository.findAllByAcceptedTrueAndCreatedBy(userDetails.getUsername());

        for (Registration registration : registrations) {
            boardLists.addAll(boardRepository.findAllByRegistrations(registration));
        }
        for (Board board : boardLists){
            reviewLists = reviewRepository.findAllByBoardAndCreatedBy(board,userDetails.getUsername());
            if (reviewLists.isEmpty() || reviewLists == null){
                boardwithnoreview.add(board);
            }
        }
        attendedBoardList=boardwithnoreview.stream().map(
                board -> new AttendedBoardDto(
                        board.getId(),
                        board.getTitle(),
                        board.getBoard_imgUrl(),
                        board.getLocation(),
                        board.getMeetTime(),
                        board.getCreatedAt(),
                        board.getPeople_current(),
                        board.getPeople_max()
                )
        ).collect(Collectors.toList());
        return ResponseEntity.ok().body(attendedBoardList);
    }

}
