package sparta.enby.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sparta.enby.service.UserService;

import javax.servlet.http.HttpServletRequest;


@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;

    //카카오 로그인 + 사용자 정보 호출 + JWT token 제공
    @RequestMapping("/callback/kakao")
    public ResponseEntity <String> oauth2AuthroziationKakao(@RequestParam("code") String code, HttpServletRequest request){
        if (code == null || code.isEmpty()){
            return ResponseEntity.badRequest().body("인가코드가 없습니다.");
        }
        return ResponseEntity.ok().body(userService.oauth2AuthorizationKakao(code, request));
    }
}