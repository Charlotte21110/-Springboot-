package com.design.filmr.handle;

import com.design.filmr.common.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {       

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<List<Map<String,Object>>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<Map<String,Object>> list = new LinkedList<>();
        result.getFieldErrors().forEach(error -> {
            String field = error.getField();
            Object value = error.getRejectedValue();
            String msg = error.getDefaultMessage();
            Map<String,Object> data = new HashMap<>();
            data.put("field",field);
            data.put("value",value);
            data.put("msg",msg);
            list.add(data);
        });

        return  Result.fail(200003,"",list);
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<String> handleTokenException(TokenException ex) {
        return ResponseEntity.status(50008).body(ex.getMessage());
    }
}
