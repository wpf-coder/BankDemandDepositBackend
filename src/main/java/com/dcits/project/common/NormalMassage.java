package com.dcits.project.common;


import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;


/**
 * 该类为正常处理，controller返回前端数据的封装类。返回数据的时候，
 * 调用该类的方法 toMap即可
 * @version: 1.0
**/
@Getter
@Setter
public class NormalMassage {
    public static final int NORMAL_STATE = 0;
    public static final int ERROR_STATE  = 1;

    private String massage;
    private int state;
    private Object data;

    public NormalMassage(){
        state = NormalMassage.NORMAL_STATE;
        massage = "操作成功";
    }

    public NormalMassage(String massage, int state, Object data) {
        this.massage = massage;
        this.state = state;
        this.data = data;
    }

    /**
     * 正常处理，但附有数据的返回数据封装的构造方法。
     * @how:
     *
     * @return:
     *
    **/
    public NormalMassage(Object data){
        this.data = data;
        massage = null;
        state   = NormalMassage.NORMAL_STATE;
    }

    /**
     * 正常处理，仅返回一个成功信息的构造方法
     * @how:
     *
     * @return:
     *
    **/
    public NormalMassage(String massage){
        this.data = null;
        this.state = NormalMassage.NORMAL_STATE;
        this.massage = massage;
    }


    /**
     * 统一正常返回的接口
     * @how:
     *      目前只简单代码命令上枚举生成对应的 map
     * @return:
     *       java.util.Map<java.lang.String,java.lang.Object>
    **/
    public Map<String,Object> toMap(){
        JSONObject map = new JSONObject();
        map.put("state",state);
        map.put("message",massage);
        map.put("data",data);
        return map;
    }
}
