package com.lets.exception;



import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;




@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value = {ConstraintViolationException.class, MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(Exception e){
        log.error("handleMethodArgumentNotValidException throw CustomException : {}", e.getMessage());
        return ErrorResponse.toResponseEntity(ErrorCode.valueOf("INVALID_INPUT_VALUE"));
    }


    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        log.error("handleHttpRequestMethodNotSupportedException throw CustomException : {}", e.getMessage());
        return ErrorResponse.toResponseEntity(ErrorCode.valueOf("METHOD_NOT_ALLOWED"));

    }
    @ExceptionHandler(value = {CustomException.class})
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e){
        log.error("handleCustomException throw CustomException : {}", e.getErrorCode());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

}
