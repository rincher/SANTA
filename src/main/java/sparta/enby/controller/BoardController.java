package sparta.enby.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sparta.enby.dto.*;
import sparta.enby.security.UserDetailsImpl;
import sparta.enby.service.BoardService;
import sparta.enby.service.RegistrationService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final RegistrationService registrationService;

    //게시글 리스트
    @GetMapping("/main/board")
    public ResponseEntity getBoardList() {
        return boardService.getBoardList();
    }

    //게시글 페이지
    @GetMapping("/board/mating")
    public Page<BoardResponseDto> getBoard(@RequestParam("page") int page, @RequestParam("size") int size, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.getBoard(page, size, userDetails);
    }

    //게시판 상세
    @GetMapping("/board/mating/{board_id}")
    public ResponseEntity getDetailBoard(@PathVariable Long board_id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.getDetailBoard(board_id, userDetails);
    }

    //게시글 적기
    @PostMapping("/board/mating")
    public ResponseEntity writeBoard(@ModelAttribute BoardRequestDto boardRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        if (boardRequestDto.getContents() == null || boardRequestDto.getContents().isEmpty()) {
            return new ResponseEntity<>("내용을 기입해주세요", HttpStatus.BAD_REQUEST);
        }
        if (boardRequestDto.getLocation() == null || boardRequestDto.getLocation().isEmpty()) {
            return new ResponseEntity<>("만날 장소를 올려주세요", HttpStatus.BAD_REQUEST);
        }
        if (boardRequestDto.getTitle() == null || boardRequestDto.getTitle().isEmpty()) {
            return new ResponseEntity<>("제목을 입력해 주세요", HttpStatus.BAD_REQUEST);
        }
        if (boardRequestDto.getMeetTime() == null || boardRequestDto.getMeetTime().isEmpty()) {
            return new ResponseEntity<>("모임 시간을 설정해주세요", HttpStatus.BAD_REQUEST);
        }
        if (boardRequestDto.getPeople_max() != 4) {
            boardRequestDto.setPeople_max(4);
        }
        //개시글 작성
        Long board_id = boardService.writeBoard(boardRequestDto, userDetails);
        //게시글 생성시 자동으로 주최자를 참여 시키기 위한 부분
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setContents("주최자 입니다");
        registerRequestDto.setAccepted(true);
        registerRequestDto.setKakao_id(userDetails.getUsername());
        //주최자 참여하기
        return registrationService.makeRegistration(registerRequestDto, board_id, userDetails);
    }

    //게시글 수정
    @PutMapping("/board/mating/{board_id}")
    public ResponseEntity editBoard(@PathVariable Long board_id, @ModelAttribute BoardRequestDto boardRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return boardService.editBoard(board_id, boardRequestDto, userDetails);
    }

    //게시글 삭제
    @DeleteMapping("/board/mating/{board_id}")
    public ResponseEntity deleteBoard(@PathVariable Long board_id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.deleteBoard(board_id, userDetails.getAccount());
    }

    //마감하기 버튼
    @PutMapping("/board/mating/{board_id}/deadline")
    public ResponseEntity<String> clickFinish(@PathVariable Long board_id, @RequestBody ChangeDeadlineRequestDto changeDeadlineRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.clickFinish(board_id, changeDeadlineRequestDto, userDetails.getAccount());
    }

    @PostMapping("/board/mating/noreviews")
    public ResponseEntity<List<AttendedBoardDto>> getBoardWithoutReview(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return boardService.getBoardWithoutReview(userDetails);
    }

}