package com.hcommerce.heecommerce.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDto {
    private final String code;
    private final String message;
}
