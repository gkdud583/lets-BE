package com.lets.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  /* 400 BAD_REQUEST : 잘못된 요청 */
  USER_ID_NOT_THE_SAME(BAD_REQUEST, "로그인 정보[ID]가 올바르지 않습니다."),
  SOCIAL_LOGIN_ID_AND_AUTH_PROVIDER_NOT_THE_SAME(
      BAD_REQUEST,
      "로그인 정보[SOCIAL_LOGIN_ID, AUTH_PROVIDER]가 올바르지 않습니다."
  ),
  REFRESH_TOKEN_NOT_FOUND(BAD_REQUEST, "쿠키에 REFRESH_TOKEN이 존재하지 않습니다."),

  //    NOT_SUPPORTED_AUTH_PROVIDER(BAD_REQUEST, "지원하지 않는 소셜 로그인입니다."),
  TAG_NOT_FOUND(BAD_REQUEST, "존재하지 않는 태그입니다."),
  INVALID_INPUT_VALUE(BAD_REQUEST, "요청 DTO 바인딩 예외입니다."),
  METHOD_NOT_ALLOWED(BAD_REQUEST, "잘못된 HTTP METHOD 요청입니다."),

  /* 401 UNAUTHORIZED  : 인증되지 않은 사용자 */
  ACCESS_TOKEN_NOT_FOUND(UNAUTHORIZED, "요청 헤더에 ACCESS_TOKEN이 존재하지 않습니다."),
  INVALID_REFRESH_TOKEN(UNAUTHORIZED, "REFRESH_TOKEN이 유효하지 않습니다."),
  UNAUTHORIZED_USER(UNAUTHORIZED, "접근 권한이 없습니다."),

  /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
  USER_NOT_FOUND(NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다."),
  POST_NOT_FOUND(NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
  COMMENT_NOT_FOUND(NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),

  /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
  DUPLICATE_NAME(CONFLICT, "중복된 닉네임입니다."),
  DUPLICATE_ACCOUNT(CONFLICT, "이미 가입된 계정이 있습니다."),

  /* 500 INTERVAL_SERVER_ERROR : 내부 서버 오류 */
  CLOUDINARY_ERROR(INTERNAL_SERVER_ERROR, "CLOUDNINARY에서 예외가 발생했습니다."),
  MULTIPARTFILE_TO_FILE_ERROR(INTERNAL_SERVER_ERROR, "MultipartFileToFile 변환에서 예외가 발생했습니다.");
  //    REDIS_ERROR(INTERNAL_SERVER_ERROR, "[REDIS]에서 예외가 발생했습니다."),

  private final HttpStatus httpStatus;
  private final String detail;

}
