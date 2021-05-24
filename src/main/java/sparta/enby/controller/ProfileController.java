package sparta.enby.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sparta.enby.security.UserDetailsImpl;
import sparta.enby.service.ProfileService;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    //사용자 프로필
    @GetMapping("/mypage/{name}")
    public ResponseEntity<Object> getProfile(@PathVariable String name, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return profileService.getProfile(name, userDetails);
    }
}
