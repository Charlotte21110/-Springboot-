package com.design.filmr.handle;

@TokenExceptionStatus
public class TokenException extends RuntimeException{
    public TokenException(String message){
        super(message);
    }
}
