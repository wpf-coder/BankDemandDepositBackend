package com.dcits.project.exception;


import com.dcits.project.common.NormalMassage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 该类是请求路径中一旦有任何不对的情况，则按照state、massage的规则进行构造，并抛出。
 * 具体如何反馈给前端，由Handle进行处理。
 * @version: 1.0
 **/

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RequestHandleException extends RuntimeException{
    public static final int ERROR_STATE = 1;


    private NormalMassage normalMassage;

    public RequestHandleException(String massage){
        normalMassage = new NormalMassage(massage);
        normalMassage.setState(NormalMassage.ERROR_STATE);
    }

    public RequestHandleException(int state,String massage){
        normalMassage = new NormalMassage(massage,state,null);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"normalMassage\":")
                .append(normalMassage);
        sb.append('}');
        return sb.toString();
    }
}
