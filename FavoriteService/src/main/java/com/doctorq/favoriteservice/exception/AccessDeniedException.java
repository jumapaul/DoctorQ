package com.doctorq.favoriteservice.exception;

public class AccessDeniedException extends RuntimeException{
    public AccessDeniedException(String message){
        super(message);
    }
}
