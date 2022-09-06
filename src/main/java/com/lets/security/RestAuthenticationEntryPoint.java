package com.lets.security;

import com.lets.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {

         ErrorCode errorCode = (ErrorCode) httpServletRequest.getAttribute("errorCode");
        log.error("RestAuthenticationEntryPoint throw CustomException : {}", errorCode.getHttpStatus().name());

        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.sendError(errorCode.getHttpStatus().value(), errorCode.getDetail());

    }

}