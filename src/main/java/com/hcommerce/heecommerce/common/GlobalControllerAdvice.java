package com.hcommerce.heecommerce.common;

import com.hcommerce.heecommerce.common.dto.ResponseDto;
import com.hcommerce.heecommerce.order.InvalidPaymentAmountException;
import com.hcommerce.heecommerce.order.MaxOrderQuantityExceededException;
import com.hcommerce.heecommerce.order.OrderNotFoundException;
import com.hcommerce.heecommerce.order.OrderOverStockException;
import com.hcommerce.heecommerce.order.TimeDealProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ResponseDto orderNotFoundExceptionHandler(OrderNotFoundException e) {
        return ResponseDto.builder()
                .code(HttpStatus.NOT_FOUND.name())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ResponseDto timeDealProductNotFoundExceptionHandler(TimeDealProductNotFoundException e) {
        return ResponseDto.builder()
            .code(HttpStatus.NOT_FOUND.name())
            .message(e.getMessage())
            .build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ResponseDto orderOverStockExceptionExceptionHandler(OrderOverStockException e) {
        return ResponseDto.builder()
                .code(HttpStatus.CONFLICT.name())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ResponseDto maxOrderQuantityExceededExceptionHandler(MaxOrderQuantityExceededException e) {
        return ResponseDto.builder()
            .code(HttpStatus.CONFLICT.name())
            .message(e.getMessage())
            .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseDto methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        return ResponseDto.builder()
            .code(HttpStatus.BAD_REQUEST.name())
            .message(e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
            .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseDto invalidPaymentAmountExceptionHandler(InvalidPaymentAmountException e) {
        return ResponseDto.builder()
            .code(HttpStatus.BAD_REQUEST.name())
            .message(e.getMessage())
            .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ResponseDto fallbackExceptionHandler(Exception e) {
        log.error("class = {}, message = {}, cause = {}", e.getClass(), e.getMessage(), e.getCause());
        log.debug("stackTrace = {}", e.getStackTrace());

        return ResponseDto.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .message("내부 서버 오류가 발생했습니다.")
                .build();
    }
}
