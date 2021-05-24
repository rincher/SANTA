package sparta.enby.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sparta.enby.dto.RegisterRequestDto;
import sparta.enby.model.Account;
import sparta.enby.model.Board;
import sparta.enby.model.Registration;
import sparta.enby.repository.AccountRepository;
import sparta.enby.repository.BoardRepository;
import sparta.enby.repository.RegistrationRepository;
import sparta.enby.security.UserDetailsImpl;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final BoardRepository boardRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public ResponseEntity makeRegistration(RegisterRequestDto registerRequestDto, Long board_id, UserDetailsImpl userDetails) {
        Board board = boardRepository.findById(board_id).orElse(null);
        Registration registration = registrationRepository.findAllByBoardIdAndCreatedBy(board_id, userDetails.getUsername());
        Account account = accountRepository.findByNickname(userDetails.getUsername()).orElse(null);
        if (board == null) {
            return new ResponseEntity<>("없는 게시글입니다", HttpStatus.BAD_REQUEST);
        }

        if (registration != null && registration.getAccount().equals(account)) {
            return new ResponseEntity<>("중복 요청은 안됩니다.", HttpStatus.BAD_REQUEST);
        }
        Boolean allowed;
        if (registerRequestDto.isAccepted() == true){
            allowed = true;
        }
        else{
            allowed = false;
        }
        Registration newRegistration = Registration.builder()
                .contents(registerRequestDto.getContents())
                .kakao_id(registerRequestDto.getKakao_id())
                .account(account)
                .accepted(allowed)
                .build();
        if (newRegistration.isAccepted()){
            int i = board.getPeople_current();
            if (board.getPeople_current() >= board.getPeople_max()){
                return ResponseEntity.badRequest().body("모집 인원이 다 찼습니다.");
            }
            else{
                i = i + 1;
                board.setPeople_current(i);
                registrationRepository.save(newRegistration);
            }
        }
        newRegistration.addBoardAndAccount(board, account);
        return new ResponseEntity<>("신청을 성공 하였습니다. registration id: " + newRegistration.getId(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity acceptRegistration(RegisterRequestDto registerRequestDto, Long board_id, Long register_id, UserDetailsImpl userDetails) {
        Board board = boardRepository.findById(board_id).orElse(null);
        if (board == null) {
            return ResponseEntity.badRequest().body("없는 게시글입니다.");
        }
        Registration registration = registrationRepository.findById(register_id).orElse(null);
        Account account = accountRepository.findByNickname(userDetails.getUsername()).orElse(null);
        if (!account.getNickname().equals(board.getCreatedBy())) {
            return ResponseEntity.badRequest().body("주최자만 신청을 허락할 수 있습니다.");
        }
        registration.update(registerRequestDto);
        return ResponseEntity.ok().body("신청을 허용하였습니다");
    }

    @Transactional
    public ResponseEntity declinedRegistration(Long board_id, Long register_id, UserDetailsImpl userDetails) {
        Board board = boardRepository.findById(board_id).orElse(null);
        if (board == null) {
            return ResponseEntity.badRequest().body("없는 게시판입니다");
        }
        Registration registration = registrationRepository.findById(register_id).orElse(null);
        if (registration == null) {
            return ResponseEntity.badRequest().body("없는 신청입니다");
        }
        Account account = accountRepository.findByNickname(userDetails.getUsername()).orElse(null);
        if (board.getCreatedBy().equals(account.getNickname())) {
            int i = board.getPeople_current();
            i = i - 1;
            board.setPeople_current(i);
            registrationRepository.deleteById(register_id);
            return ResponseEntity.ok().body("요청을 거절 하였습니다");
        }
        if (registration.getCreatedBy().equals(account.getNickname())){
            int i = board.getPeople_current();
            i = i - 1;
            board.setPeople_current(i);
            registrationRepository.deleteById(register_id);
            return ResponseEntity.ok().body("요청을 취소 하였습니다");
        }
        return ResponseEntity.ok().body("취소는 주최자 또는 신청자 본인만 가능합니다.");
    }
}
