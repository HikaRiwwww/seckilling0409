package com.throne.seckilling.controller;

import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.error.EnumBusinessError;
import com.throne.seckilling.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller的基类
 * 其中定义了处理异常的方法
 * com.throne.seckilling.controller
 * Created by throne on 2020/4/11
 */
public class BaseController {
    public static final String CONTENT_TYPE_FORMED = "application/x-www-form-urlencoded";

//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public Object handleException(HttpServletRequest req, Exception ex){
//        Map<String, Object> errorResp = new HashMap<>();
//        if (ex instanceof BusinessException){
//            BusinessException businessException = (BusinessException) ex;
//            errorResp.put("errorCode", businessException.getErrorCode());
//            errorResp.put("errorMsg", businessException.getErrorMsg());
//        }else {
//            errorResp.put("errorCode", EnumBusinessError.UNKNOWN_ERROR.getErrorCode());
//            errorResp.put("errorMsg",EnumBusinessError.UNKNOWN_ERROR.getErrorMsg() );
//        }
//        return CommonReturnType.create("fail", errorResp);
//    }
}
