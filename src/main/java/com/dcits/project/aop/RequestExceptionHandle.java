package com.dcits.project.aop;

import com.dcits.internal.flowHandle.FlowHandleException;
import com.dcits.project.common.NormalMassage;
import com.dcits.project.exception.RequestHandleException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@ControllerAdvice
public class RequestExceptionHandle {

    @ExceptionHandler(RequestHandleException.class)
    @ResponseBody
    public Map<String,Object> handleRequestException(RequestHandleException e){

        return e.getNormalMassage().toMap();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Map<String,Object> handleIllegalArgumentExceptionException(IllegalArgumentException e){
        System.out.println("e.getMessage() = " + e.getMessage());
        NormalMassage normalMassage = new NormalMassage("参数不正确",NormalMassage.ERROR_STATE,null);
        return normalMassage.toMap();
    }

    @ExceptionHandler(FlowHandleException.class)
    @ResponseBody
    public Map<String,Object> handleIllegalArgumentExceptionException(FlowHandleException e){
        System.out.println("e.getMessage() = " + e.getMessage());
        NormalMassage normalMassage = new NormalMassage("系统繁忙，请稍候再试。",NormalMassage.ERROR_STATE,null);
        return normalMassage.toMap();
    }

}
