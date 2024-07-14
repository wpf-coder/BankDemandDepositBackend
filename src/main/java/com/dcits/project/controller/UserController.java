package com.dcits.project.controller;

import com.alibaba.fastjson.JSONObject;
import com.dcits.project.aop.Authority;
import com.dcits.project.aop.Role;
import com.dcits.project.common.DateUtil;
import com.dcits.project.common.NormalMassage;
import com.dcits.project.common.Util;
import com.dcits.project.exception.RequestHandleException;
import com.dcits.project.pojo.*;
import com.dcits.project.service.CardService;
import com.dcits.project.service.ClientInformationService;
import com.dcits.project.service.TransactionFlowService;
import com.dcits.project.service.UserCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@CrossOrigin
public class UserController {
    @Autowired
    UserCommonService userCommonService;
    @Autowired
    TransactionFlowService transactionFlowService;
    @Autowired
    private CardService cardService;
    @Autowired
    ClientInformationService clientInformationService;

    /**
     * 用户登录
     * @how:
     * @Param
     *      map：
     *          id：身份证号码
     *          password：密码
     *          phone：手机号
     *          verificationCode:验证码
     *          loginType:登录方式  0 身份证号码 密码   1手机号验证码
     * @return:
     *         state：0 登录成功  1登录失败
     *         message：失败信息
    **/
    @RequestMapping("/user/login/client")
    public Map<String,Object> login(@RequestBody Map<String,String> map, HttpSession session){
        User user = null;

        //检测参数
        if(map.get("loginType")==null || map.get("loginType").equals("")){
            throw new RequestHandleException("参数异常");
        }

        User sessionUser=null;
        if(session.getAttribute("user") instanceof User){
            sessionUser=(User) session.getAttribute("user");
        }

        //身份证号码登录
        if(map.get("loginType").equals("0")){
            if(map.get("id")==null||map.get("id").equals("")
                    ||map.get("password")==null||map.get("password").equals("")){
                throw new RequestHandleException("参数异常");
            }
            user=userCommonService.userIdLogin(map.get("id"),map.get("password"),sessionUser);

        }

        //手机号登录
        if(map.get("loginType").equals("1")){
            if(session.getAttribute("validationCode")==null){
                throw new RequestHandleException("未发送验证码! 点击发送验证码");
            }
            if(map.get("phone")==null||map.get("phone").equals("")
                    ||map.get("verificationCode")==null||map.get("verificationCode").equals("")){
                throw new RequestHandleException("参数异常");
            }

            user=userCommonService.userPhoneLogin(
                    map.get("phone"),
                    map.get("verificationCode"),
                    (String) session.getAttribute("validationCode"),
                    sessionUser);
            if(user != null)
                session.removeAttribute("validationCode");

        }
        //若登录成功
        if(user!=null){
            //判断是否要覆盖 session中user的信息。
            if(!user.equals(session.getAttribute("user"))){
                session.setAttribute("user",user);
            }
            //判断密码是否需要重置
            if(map.get("password").equals(EbankUser.getInitPassword(user))){
                return new NormalMassage("登录成功",NormalMassage.NORMAL_STATE,"reset").toMap();
            }
            return new NormalMassage("登录成功").toMap();
        }

        throw new RequestHandleException(1,"登录失败!");
    }

    /**
     * 用户登录，发送验证码
     * @param map
     *          phone 手机号
     * @param session
     */
    @RequestMapping("/user/login/client/sendVerificationCode")
    public Map<String,Object> sendVerifyCode(@RequestBody JSONObject map, HttpSession session){
        if(map.getString("phone")==null)throw new RequestHandleException("参数异常");

        //验证手机号是否开通网银
        clientInformationService.phoneEbankValidation(map.getString("phone"));

        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
        session.setAttribute("validationCode",verifyCode);
        System.out.println("发送 \"登录\" 验证码 "+map.getString("phone")+" 验证码 : "+verifyCode);
        return new NormalMassage("验证码发送成功,请注意查收！").toMap();
    }

    /**
     * 网银退出登录
     * @param session
     * @return
     */
    @RequestMapping("/user/logout/client")
    public Map<String,Object> logout( HttpSession session){
        Object object=session.getAttribute("user");
        if(object!=null && object instanceof User){
            User user=(User) object;
            userCommonService.userLogout(user);
            session.removeAttribute("user");
            return new NormalMassage("退出成功！").toMap();
        }
        return new NormalMassage(NormalMassage.ERROR_STATE).toMap();
    }


    /**************************************柜员登录**********************************/


     /**
      * 柜员登录
     **/
    @RequestMapping("/user/login/teller")
    public Map<String,Object> tellerLogin(@RequestBody Teller teller, HttpSession session){
        Teller teller1=userCommonService.tellerLogin(teller.getId(),teller.getPassword());
        if (teller1!=null){
            session.setAttribute("user",teller1);
            return new NormalMassage("登录成功").toMap();
        }
        return null;
    }
}
