package com.lets.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ErrorResponse {
    private LocalDateTime timestamp = LocalDateTime.now();
    private String error;
    private String message;

    @Builder
    private ErrorResponse(String error, String message){
        this.error = error;
        this.message = message;
    }
    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode){
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .error(errorCode.getHttpStatus().name())
                        .message(errorCode.getDetail())
                        .build()
                );

    }
}
