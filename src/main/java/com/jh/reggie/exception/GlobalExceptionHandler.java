package com.jh.reggie.exception;

import com.jh.reggie.commons.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author JH
 * @description 全局异常处理
 * @date 2022-12-06 19:15:50
 */
@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<?> exceptionHandler(SQLIntegrityConstraintViolationException e) {
        log.info("Exception: {}", e.getMessage());
        if (e.getMessage().contains("Duplicate entry")) {
            String[] split = e.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    @ExceptionHandler(CustomException.class)
    public R<?> runtimeException(CustomException e) {
        log.info("CustomException: {}", e.getMessage());
        return R.error(e.getMessage());
    }
}
