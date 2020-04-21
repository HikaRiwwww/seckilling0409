package com.throne.seckilling.controller;

import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.error.EnumBusinessError;
import com.throne.seckilling.response.CommonReturnType;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * com.throne.seckilling.controller
 * Created by throne on 2020/4/21
 */
@ControllerAdvice
public class GlobalExceptionHandler {

   @ExceptionHandler(Exception.class)
   @ResponseBody
   public CommonReturnType dealWithError(HttpServletRequest request, HttpServletResponse response, Exception e){
       e.printStackTrace();
       Map<String, Object> responseData = new HashMap<>();
       if (e instanceof BusinessException){
           BusinessException businessException = (BusinessException) e;
           responseData.put("errorCode", businessException.getErrorCode());
           responseData.put("errorMsg", businessException.getErrorMsg());
       }else if (e instanceof ServletRequestBindingException){
           responseData.put("errorCode", EnumBusinessError.UNKNOWN_ERROR.getErrorCode());
           responseData.put("errorMsg", "url绑定路由问题");
       }else if (e instanceof NoHandlerFoundException){
           responseData.put("errorCode", EnumBusinessError.UNKNOWN_ERROR.getErrorCode());
           responseData.put("errorMsg", "访问路径不存在");
       }else {
           responseData.put("errorCode", EnumBusinessError.UNKNOWN_ERROR.getErrorCode());
           responseData.put("errorMsg", EnumBusinessError.UNKNOWN_ERROR.getErrorMsg());
       }
       return CommonReturnType.create("fail", responseData);
   }
}
