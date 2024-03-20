package com.design.filmr.handle;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public @interface TokenExceptionStatus {
    int value() default 50008;
}