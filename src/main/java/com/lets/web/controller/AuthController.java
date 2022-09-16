package com.lets.web.controller;

import static com.lets.exception.ErrorCode.*;

import java.io.File;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lets.domain.user.User;
import com.lets.exception.CustomException;
import com.lets.security.JwtAuthentication;
import com.lets.security.JwtTokenProvider;
import com.lets.security.UserPrincipal;
import com.lets.service.user.UserService;
import com.lets.util.CloudinaryUtil;
import com.lets.util.CookieUtil;
import com.lets.util.FileUtil;
import com.lets.util.RedisUtil;
import com.lets.web.dto.auth.AuthResponseDto;
import com.lets.web.dto.auth.LoginRequestDto;
import com.lets.web.dto.auth.SignInResponseDto;
import com.lets.web.dto.auth.SignupRequestDto;
import com.lets.web.dto.auth.SignupResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController()
public class AuthController {

  private final JwtTokenProvider jwtTokenProvider;
  private final CookieUtil cookieUtill;
  private final RedisUtil redisUtil;
  private final FileUtil fileUtil;
  private final CloudinaryUtil cloudinaryUtil;
  private final UserService userService;

  /**
   *  access token 재발급
   */
  @PostMapping("/silent-refresh")
  public AuthResponseDto getAccessToken(HttpServletRequest request) {

    /**
     * cookieUtil.getCookie()를 Optional()로 반환하게 해서 더 가독성 있게 예외처리를 할 수 있지만,
     * 그럼 해당 함수를 사용하는 JwtAuthenticationFilter에서 Optional()이 NULL일때 request.setAttribute()를 호출 하기 위해
     * 매번 IfPresent()를 사용해서 확인을 해줘야한다.
     * 근데 IfPresent()가 if( ? == null) 보다 코드 가독성 부분에서 나아지는게 없고 오히려 value를 wrapping하고 다시 까서 값을 찾아야
     * 하기 때문에 성능이 저하 될 수 있다.
     * 그래서 사용하지 않는 것으로 결정.
     */
    //refresh token 얻어옴
    Cookie refreshTokenCookie = cookieUtill.getCookie(request);
    if (refreshTokenCookie == null) {
      throw new CustomException(REFRESH_TOKEN_NOT_FOUND);
    }
    String refreshToken = refreshTokenCookie.getValue();

    //refresh token 이 레디스에 존재하는지 확인 -> 유저가 로그아웃 후에 refresh token 으로 access token 재발급 요청하면 막아야함.
    if (redisUtil.getData(refreshToken) == null) {
      throw new CustomException(INVALID_REFRESH_TOKEN);
    }

    //refresh token 유효성 확인
    boolean isValidate = jwtTokenProvider.validateToken(refreshToken);

    if (!isValidate) {
      throw new CustomException(INVALID_REFRESH_TOKEN);
    }

    //유저 정보 조회
    Long userId = jwtTokenProvider.getUserIdFromJWT(refreshToken);
    User user = userService.findById(userId);

    //access token 발행
    Authentication authentication = jwtTokenProvider.getAuthenticationFromJWT(refreshToken);
    String accessToken = jwtTokenProvider.generateAccessToken(authentication);

    //응답
    return AuthResponseDto.from(user.getNickname(), accessToken, "OK");
  }

  /**
   * 회원가입시 닉네임 중복 확인
   */
  @GetMapping("/exists")
  public void validateNickname(@RequestParam(required = true) String nickname) {
    userService.validateNickname(nickname);
  }

  /**
   * 회원가입
   */

  @PostMapping("/signup")
  public SignupResponseDto signup(@Valid @RequestBody SignupRequestDto signupRequest) {

    File file = null;
    if (!signupRequest
        .getProfile()
        .equals("PUBLIC")) {
      //file변환
      file = fileUtil.decodeFile(signupRequest.getProfile());
    }

    User saveUser = userService.signup(signupRequest, file);

    //프로필 URI
    String profile = cloudinaryUtil.findFileURL(saveUser.getPublicId());

    return SignupResponseDto.from(saveUser.getId(), profile, saveUser.getNickname(),
                                  saveUser.getSocialLoginId(), saveUser.getAuthProvider()
    );
  }

  /**
   * 로그인
   */
  @PostMapping("/signin")
  public SignInResponseDto signIn(
      @Valid @RequestBody LoginRequestDto loginRequest,
      HttpServletResponse response
  ) {

    User findUser = userService.findBySocialLoginIdAndAuthProvider(
        loginRequest.getSocialLoginId(),
        loginRequest.getAuthProvider()
    );

    //access token, refresh token 생성
    UserPrincipal principal = UserPrincipal.create(findUser);
    Authentication jwtAuthentication = new JwtAuthentication(principal);
    String accessToken = jwtTokenProvider.generateAccessToken(jwtAuthentication);
    String refreshToken = jwtTokenProvider.generateRefreshToken(jwtAuthentication);

    //레디스에 refresh token 저장
    redisUtil.setData(refreshToken, Long.toString(principal.getId()));

    //refresh token 쿠키 생성 후 응답에 추가
    Cookie refreshTokenCookie = cookieUtill.createCookie("refreshToken", refreshToken);
    // response.addCookie(refreshTokenCookie);
    response.setHeader(
        "Set-Cookie",
        "refreshToken=" + refreshToken + "; Secure; SameSite=none; HttpOnly=true; Path=/; Expires="
            + refreshTokenCookie.getMaxAge() + ";"
    );

    //프로필 URI
    String profile = cloudinaryUtil.findFileURL(findUser.getPublicId());

    return SignInResponseDto.from(profile, principal.getUsername(), accessToken, "OK");
  }

  /**
   * 로그아웃
   */
    /*
    //==flow==//
    1. 클라이언트에서 헤더에 access token 과 함께 요청
    2. 서버는 레디스에 존재하는 refresh token 을 지워서 access token 재발급을 막는다.
    3. 클라이언트는 서버에서 정상 응답이 오면 access token 을 지운다.
     */
  @PostMapping("/logout")
  @PreAuthorize("hasRole('ROLE_USER')")
  public void logout(HttpServletRequest request) {
    //쿠키에서 refresh token 을 찾는다.
    Cookie refreshTokenCookie = cookieUtill.getCookie(request);

    //쿠키에 refresh token 이 없다면 예외 던짐
    if (refreshTokenCookie == null) {
      throw new CustomException(REFRESH_TOKEN_NOT_FOUND);
    }

    //refresh token 값을 얻어 레디스에서 지운다.
    String refreshToken = refreshTokenCookie.getValue();
    redisUtil.deleteData(refreshToken);
  }

  /**
   * 탈퇴
   */

    /*
    //==flow==//
    1. 클라이언트에서 access token 과 함께 요청
    2. 서버는 사용자 계정을 삭제한다.
    3. 서버는 레디스에 존재하는 refresh token 을 지워서 access token 재발급을 막는다.
    4. 클라이언트는 서버에서 정상 응답이 오면 access token 을 지운다.
     */
  @PostMapping("/signout")
  @PreAuthorize("hasRole('ROLE_USER')")
  public void signOut(
      @AuthenticationPrincipal UserPrincipal principal,
      HttpServletRequest request
  ) {

    //유저 존재하는지 확인
    userService.existsById(principal.getId());

    //유저 삭제
    User user = userService.findById(principal.getId());
    userService.signout(user);

    //refresh token 삭제
    Cookie refreshTokenCookie = cookieUtill.getCookie(request);
    if (refreshTokenCookie == null) {
      throw new CustomException(REFRESH_TOKEN_NOT_FOUND);
    }
    String refreshToken = refreshTokenCookie.getValue();

    redisUtil.deleteData(refreshToken);
  }
}
