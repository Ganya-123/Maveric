package com.maveric.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class ErrorDto {
    private HttpStatus status;
    private String message;
    private List<String> errors;

    public ErrorDto(HttpStatus status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }
}
