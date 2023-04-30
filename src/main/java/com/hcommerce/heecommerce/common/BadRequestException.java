package com.hcommerce.heecommerce.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "잘못된 요청 요류")
public class BadRequestException extends RuntimeException {
}
