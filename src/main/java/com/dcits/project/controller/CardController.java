package com.dcits.project.controller;

import com.dcits.project.aop.Authority;
import com.dcits.project.aop.Role;
import com.dcits.project.common.NormalMassage;
import com.dcits.project.exception.RequestHandleException;
import com.dcits.project.pojo.Teller;
import com.dcits.project.pojo.User;
import com.dcits.project.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/card")
public class CardController {

    @Autowired
    private CardService service;


    /**
     * 银行卡信息查询，曾是客户接口，现在是登录用户均可使用的接口。
     * 该函数被多次修改，导致已经溃烂不堪，需要拆成两个接口————客户与柜员分别一个。
     * @return:
     *       java.util.Map<java.lang.String,java.lang.Object>
    **/
    @Authority(role = Role.LOGIN)
    @RequestMapping("/information/query")
    public Map<String,Object> queryInformation(@RequestBody(required = false) Map<String,String> params,
                                               @Autowired HttpSession session){
        String condition = null;
        Object user = session.getAttribute("user");
        Object data = null;
        //  TODO  返回user信息
        //判断Parmas是否为空
        if(params==null){
            if(user instanceof User ){  //无参查询
                data=service.queryCardInformation((User) user);
                NormalMassage normalMassage = new NormalMassage(null,NormalMassage.NORMAL_STATE,data);
                return normalMassage.toMap();
            }
        }

        //param不为null
        int type = Integer.parseInt(params.get("searchType"));
        if( (user instanceof User &&
                        (type == 0 && (condition = params.get("idCard"))!=null)         //如果是 身份证
                        || (type == 1 && (condition = params.get("phone"))!=null)       //如果是 电话号码
                    )
        ){
            data = (service.queryCardInformation(condition,type,(User)user)).get(0);
        }else if(user instanceof Teller &&(
                    type == 2 && (condition = params.get("bankCard"))!= null
                )){
            data = service.queryCardInformation(condition);
        }
        System.out.println("data = " + data);
        if(data == null)
            throw new RequestHandleException(NormalMassage.ERROR_STATE,"查询方式错误");
        NormalMassage normalMassage = new NormalMassage(null,NormalMassage.NORMAL_STATE,data);
        return normalMassage.toMap();
    }
}
