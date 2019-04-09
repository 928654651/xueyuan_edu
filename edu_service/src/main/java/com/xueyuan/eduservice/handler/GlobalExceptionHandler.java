package com.xueyuan.eduservice.handler;

import com.xueyuan.result.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    //1 处理所有的异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R error(Exception e) {
        e.printStackTrace();
        return R.error().message("出现了异常");
    }

    //2 处理特定的异常
    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public R error(ArithmeticException e) {
        e.printStackTrace();
        return R.error().message("0不能作为除数");
    }

    //4 使用自定义异常
    @ExceptionHandler(EduException.class)
    @ResponseBody
    public R error(EduException e) {
        return R.error().code(e.getCode()).message(e.getMessage());
    }
}
