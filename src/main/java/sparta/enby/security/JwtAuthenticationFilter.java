package sparta.enby.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
// SpringSecurity addFilterBefore 에서 filter 역활
public class JwtAuthenticationFilter extends GenericFilterBean {

    // Jwt token을 만들어 주는 모듈
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // http request header "Authorization" 에 있는 value를 가져와서
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        // JwtTokenProvider 에서 만들때 넣은 값 (secrete key, 만기 시간)을 검증
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            // SecurityContext 에 Authentication 객체를 저장합니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // chain.doFilter 를 통해서 응답을 필터링한다(?)
        chain.doFilter(request, response);
    }
}