package com.westee.wxshop.controller;

import com.westee.wxshop.entity.HttpException;
import com.westee.wxshop.entity.Response;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ErrorHandlingController {
    @ExceptionHandler(HttpException.class)
    public @ResponseBody
    Response<?> onError(HttpServletResponse response, HttpException e){
        response.setStatus(e.getStatusCode());
        return Response.of(e.getMessage(), null);
    }
}
