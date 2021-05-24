package sparta.enby.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sparta.enby.model.Account;
import sparta.enby.model.AuthorizationKakao;
import sparta.enby.security.JwtTokenProvider;
import sparta.enby.security.kakao.OAuth2Kakao;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class UserService {
    private final OAuth2Kakao oAuth2Kakao;
    private final JwtTokenProvider jwtTokenProvider;

    public String oauth2AuthorizationKakao(String code, HttpServletRequest request){
        String URL = request.getRequestURL().toString();
        AuthorizationKakao authorization = oAuth2Kakao.callTokenApi(code, URL);
        Account account = oAuth2Kakao.callGetUserByAccessToken(authorization.getAccess_token());
        return jwtTokenProvider.createToken(account.getNickname(), account.getProfile_img(), account.getRoles());
    }
}
