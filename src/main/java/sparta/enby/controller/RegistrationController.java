package sparta.enby.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sparta.enby.dto.RegisterRequestDto;
import sparta.enby.security.UserDetailsImpl;
import sparta.enby.service.RegistrationService;

@RestController
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    //참여하기
    @PostMapping("/board/mating/{board_id}/register")
    public ResponseEntity makeRegistration(@ModelAttribute RegisterRequestDto registerRequestDto, @PathVariable Long board_id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return registrationService.makeRegistration(registerRequestDto, board_id, userDetails);
    }

    //주최자가 참여 허락
    @PutMapping("/board/mating/{board_id}/register/{register_id}")
    public ResponseEntity acceptRegistration(@RequestBody RegisterRequestDto registerRequestDto, @PathVariable Long board_id, @PathVariable Long register_id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return registrationService.acceptRegistration(registerRequestDto, board_id,register_id, userDetails);
    }

    //참여 취소하기 + 주최자가 참여거절
    @DeleteMapping("/board/mating/{board_id}/register/{register_id}")
    public ResponseEntity declinedRegistration(@PathVariable Long board_id, @PathVariable Long register_id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return registrationService.declinedRegistration(board_id, register_id, userDetails);
    }
}
