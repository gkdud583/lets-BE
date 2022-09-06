package com.lets.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lets.security.JwtTokenProvider;
import com.lets.security.UserPrincipal;
import com.lets.web.dto.auth.OAuth2ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long id = principal.getId();

        OAuth2ResponseDto oAuth2Response = null;
        if(id == null){
            //아직 가입 안한 유저
            oAuth2Response = new OAuth2ResponseDto(false, "회원가입이 필요합니다.", principal.getSocialLoginId(), principal.getAuthProvider());

        }else{
            oAuth2Response = new OAuth2ResponseDto(true, "로그인 해주세요.", principal.getSocialLoginId(), principal.getAuthProvider());

        }
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(oAuth2Response);

        /**한글 깨짐**/
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        response.getWriter().println(jsonInString);
    }
}
