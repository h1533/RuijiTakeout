package com.jh.reggie.exception;

/**
 * @author JH
 * @description 业务异常
 * @date 2022-12-07 21:15:57
 */
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}
