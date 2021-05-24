package sparta.enby.security.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import sparta.enby.model.Account;
import sparta.enby.model.AuthorizationKakao;
import sparta.enby.repository.AccountRepository;

import javax.transaction.Transactional;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OAuth2Kakao {
    //RestTemplate: Json 형태로 정보를 받아오기 위해서
    private final RestTemplate restTemplate;
    //ObjectMapper: Java Object 를 Json 으로 변화해주기 위해서
    private final ObjectMapper objectMapper;
    private final AccountRepository accountRepository;


    // Kakao Authorization code
    public AuthorizationKakao callTokenApi(String code, String URL) {
        String grantType = "authorization_code";

        HttpHeaders headers = new HttpHeaders();
        // application/x-www-form-urlencoded content type
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //URL parameter로 엮어 주는 부분
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        //사용자 정보를 가져올때 필요한 access_token을 요청
        params.add("grant_type", grantType);
        //RestApiKey
        //카카오 RESTAPI Key
        String kakaoOauth2ClientId = "17fb08cb376f564b3375667a799fda1f";
        params.add("client_id", kakaoOauth2ClientId);
        if (URL.contains("http://localhost:8080")) {
            params.add("redirect_uri", "http://localhost:8080/callback/kakao");
        }
        if (URL.contains("http://localhost:3000")) {
            params.add("redirect_uri", "http://localhost:3000/oauth");
        }
        if (URL.contains("http://enby.s3-website.ap-northeast-2.amazonaws.com")) {
            params.add("redirect_uri", "http://enby.s3-website.ap-northeast-2.amazonaws.com/oauth");
        }
        if (URL.contains("http://www.santa-mountain.com/")){
            params.add("redirect_uri", "http://www.santa-mountain.com/oauth");
        }
        //인가 코드
        params.add("code", code);

        // 사용자 정보 받아 오는 부분
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        //사용자 정보를 받아 올 수 있는 카카오 API
        String url = "https://kauth.kakao.com/oauth/token";
        try {
            //카카오로 부터 받은 응답을 JSON 형식으로 변현해서 저장
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            //응답을 AuthorizationKakao에 선언해놓은 Model에 대입해서 반환
            return objectMapper.readValue(response.getBody(), AuthorizationKakao.class);
        } catch (RestClientException | JsonProcessingException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Failed");
        }
    }

    @Transactional
    //사용자 정보 가져오는 부분
    public Account callGetUserByAccessToken(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        //Kakao에서 인증하기 위해서 Authorization token을 보내줘야한다.
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        //카카오 사용자 정보 가져 올 수 있는 API
        String url = "https://kapi.kakao.com/v2/user/me";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            //읽을 수 만 있는 JSONNode로 response에서 받은 정보를 넣어서
            JsonNode root = objectMapper.readTree(response.getBody());
            //JsonNode에 있는 정보 중에서 Kakao_account 부분을 가져와서
            JsonNode kakao_account = root.path("kakao_account");
            //임의의 비밀번호를 만들어서
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String password = "AAABnv/xRVklrnYxKZ0aHgTBcXukeZygoC";
            String profile_image_url = null;
            if (kakao_account.path("profile").path("profile_image_url").asText().isEmpty() || kakao_account.path("profile").path("profile_image_url").asText() == null) {
                profile_image_url = "https://hanghae99-gitlog.s3.ap-northeast-2.amazonaws.com/unset_photo.jpeg";
            } else {
                profile_image_url = kakao_account.path("profile").path("profile_image_url").asText();
            }
            String nickname = kakao_account.path("profile").path("nickname").asText();

            Account account = accountRepository.findByNickname(kakao_account.path("profile").path("nickname").asText()).orElse(null);

            if (account == null) {
                Account newaccount = accountRepository.save(Account.builder()
                        .password(passwordEncoder.encode(password))
                        .nickname(nickname)
                        .profile_img(profile_image_url)
                        .roles(Collections.singletonList("ROLE_USER"))
                        .build());
                return newaccount;
            }
            else {
                account.update(nickname, profile_image_url);
            }
            return account;

        } catch (RestClientException | JsonProcessingException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Failed");
        }
    }
}
