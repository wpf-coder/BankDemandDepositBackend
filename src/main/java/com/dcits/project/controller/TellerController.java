package com.dcits.project.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.dcits.project.aop.Authority;
import com.dcits.project.aop.Role;
import com.dcits.project.common.DateUtil;
import com.dcits.project.common.NormalMassage;
import com.dcits.project.dto.UserEstablish;
import com.dcits.project.exception.RequestHandleException;
import com.dcits.project.mapper.LedgerRecordMapper;
import com.dcits.project.mapper.TransactionFlowMapper;
import com.dcits.project.pojo.*;
import com.dcits.project.service.*;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/teller")
@Authority(role = Role.TELLER)
public class TellerController {
    @Autowired
    ClientInformationService clientInformationService;
    @Autowired
    CardService cardService;
    @Autowired
    BranchService branchService;
    @Autowired
    SystemInformationService systemInformationService;
    @Autowired
    TransactionFlowService transactionFlowService;


    /**
     * 开户时异步验证身份证号和手机号是否存在于系统
     * @param jo 接收id 和phone
     * @return
     */
    @RequestMapping("/client/account/validation")
    public Map<String,Object> valication(@RequestBody JSONObject jo){
        String id=null;
        String phone=null;
        if(jo.getString("id")!=null){
            id=jo.getString("id");
        }
        if(jo.getString("phone")!=null){
            phone=jo.getString("phone");
        }
        clientInformationService.idValidation(id,phone);
        return new NormalMassage(NormalMassage.NORMAL_STATE).toMap();
    }

    /**
     * 开户
     * @param userEstablish 接收前端发送数据
     * @return
     *      state：0 开户成功  1 开户失败
     *      message 失败信息
     */
    @RequestMapping("/client/account/establish")
    public Map<String,Object> accountEstablish(@RequestBody UserEstablish userEstablish){

        NormalMassage normalMassage = new NormalMassage();
        //验证参数是否正确
        if(userEstablish.valicate()==false){
            throw new RequestHandleException("参数不正确");
        }
        //参数验证成功
        normalMassage.setData(clientInformationService.establishAccount(userEstablish));
        normalMassage.setState(NormalMassage.NORMAL_STATE);
        normalMassage.setMassage("开户成功");

        System.out.println(normalMassage);
        return normalMassage.toMap();
    }

     /**
      * 客户信息查询
      * @how:
      * @Param
      *       map  id 身份证号码
      * @return:
      *         返回data： user、card
     **/
    @RequestMapping("/client/service/query")
    public Map<String,Object> infoQuery(@RequestBody JSONObject map){

        if(map.getString("idCard")!=null&&!map.getString("idCard").equals("")){
            return  new NormalMassage(
                    "查询成功",
                    NormalMassage.NORMAL_STATE,
                    clientInformationService.infoQuery(map.getString("idCard"))).toMap();
        }
        throw new RequestHandleException("身份证号码参数有误！");
    }

    /**
     * 业务开通 网银 手机银行 外汇
     * @param map
     * @return
     */
    @RequestMapping("/serviceopen")
    public  Map<String,Object> serviceOpen(@RequestBody JSONObject map){

        NormalMassage normalMassage=new NormalMassage();
        normalMassage.setData(clientInformationService.serviceOpen(map));
        normalMassage.setState(NormalMassage.NORMAL_STATE);
        normalMassage.setMassage("业务开通成功");

        System.out.println(normalMassage);
        return normalMassage.toMap();
    }

    /**
     * 存款业务
     * @how:
     * @Params:
     *      cardId：     存款的银行卡卡号，必须
     *      amount：     存款的金额，必须
     * @return:
     *       java.util.Map<java.lang.String,java.lang.Object>
    **/
    //@Authority(role = Role.TELLER)
    @RequestMapping("/client/deposit")
    public Map<String,Object> clientDeposit(@RequestBody  Map<String,String> params, HttpSession session){
        String cardId = params.get("cardId");
        BigDecimal amount = new BigDecimal(params.get("amount"));
        Object teller = session.getAttribute("user");
        if(!Card.validateCardId(cardId)
                || amount.compareTo(new BigDecimal(0)) == -1){
            throw new IllegalArgumentException();
        }

        NormalMassage normalMassage = new NormalMassage();
        if(cardService.deposit(cardId,amount,((Teller)teller).getBranchId())){
            normalMassage.setState(NormalMassage.NORMAL_STATE);
        }

        return normalMassage.toMap();
    }

    /**
     * 用户取款
     * @how:
     * @Params:
     *      params 必须有
     *              cardId
     *              password
     *              amount
     * @return:
     *       java.util.Map<java.lang.String,java.lang.Object>
    **/
    @RequestMapping("/client/draw")
    public Map<String,Object> clientWithdraw(@RequestBody JSONObject params, HttpSession session){
        String cardId = params.getString("cardId");
        BigDecimal amount = params.getBigDecimal("amount");
        String password = params.getString("password");

        Object teller = session.getAttribute("user");
        if(!Card.validateCardId(cardId)
                || amount.compareTo(new BigDecimal(0)) == -1
                || password == null
                || password.equals("")){
            throw new IllegalArgumentException();
        }

        NormalMassage normalMassage = new NormalMassage();
        TransactionFlow flow = cardService.withdraw(cardId,amount,((Teller)teller).getBranchId(),password);
        normalMassage.setState(NormalMassage.NORMAL_STATE);
        normalMassage.setData(flow.getSubjectCardBalance());

        return normalMassage.toMap();
    }


     /**
      * 网点总账查询
      * @how:
      *       判断时间是否为空，不为空，从map中拿到时间
      *       获取当前可查询范围，判断两个时间是否在范围内 在 继续执行，不在报错：只能查询近30天内记录
      *       将开始时间置为开始时间当天的0点，结束时间改为结束时间的后一天0点
      *       调用service查询
      * @Param
      *      map：       接收前端传值
      *      type：      查询类型    1 现金总账  0为全部
      *      endTime：   结束时间
      *      startTime： 开始时间
     **/
    @RequestMapping("/cashLedger/query")
    public Map<String,Object> queryCashLedger(@RequestBody JSONObject map, HttpSession session){
        Timestamp startTime=null;
        Timestamp endTime=null;

        //判断map中值是否为空
        if (map.getDate("startTime")!=null){
            startTime =new Timestamp(map.getDate("startTime").getTime());
        }
        if (map.getDate("endTime")!=null){
            endTime =new Timestamp(map.getDate("endTime").getTime());
        }
        if(map.getInteger("type")==null){
            throw new RequestHandleException("查询类型为空！");
        }
        //判断时间，进行格式转变

        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        timestamp= DateUtil.lastNumDay(timestamp,30);

        if(startTime.before(timestamp)||endTime.before(timestamp)) {
            throw new RequestHandleException("只能查询近30天内记录");
        }

        endTime= DateUtil.nextDay(endTime);
        startTime=DateUtil.today(startTime);
        Teller teller=(Teller) session.getAttribute("user");
        List<LedgerRecord> list=branchService.queryCashLedger(
                teller.getBranchId(),
                map.getString("type"),startTime,endTime);
        System.out.println(list);
        return new NormalMassage(list).toMap();
    }

    /**
     *  验证银行卡和身份证号码是否匹配 匹配则返回用户信息user
     * @param map
     * @return
     */
    @RequestMapping("/client/query")
    public Map<String,Object> valicationIDandCardId(@RequestBody JSONObject map){
        Object data=null;
        if(map.getString("Id")!=null&&map.getString("cardId")!=null){
            data=clientInformationService.valicationIDandCardId(map.getString("Id"),map.getString("cardId"));
        }
        return new NormalMassage(data).toMap();
    }

    /**
     * 修改密码时发送验证码
     * @param phone
     * @param session
     */
    @RequestMapping("/client/sendVerificationCode")
    public Map<String,Object> sendCodeOfUpdatePwd(@RequestBody String phone, HttpSession session){
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
        session.setAttribute("codeOfUpdatePwd",verifyCode);
        System.out.println("发送 \"修改密码\" 验证码 "+phone+" : "+verifyCode);
        return new NormalMassage("验证码已发送至客户手机,请提醒客户查收！").toMap();
    }


    /**
     * 修改网银密码
     * @How
     *      先验证验证码，
     *      在验证身份证号码、银行卡信息
     *      修改密码
     * @param map 接受前端 身份证号码 银行卡号  要修改的密码
     * @return
     */
    @RequestMapping("/client/newloginPwd")
    public Map<String,Object> updateEbankUserPwd(@RequestBody JSONObject map,HttpSession session){
        String code=(String) session.getAttribute("codeOfUpdatePwd");
        if(code==null){
            throw new RequestHandleException("未发送验证码");
        }
        //判断参数是否异常
        if(map.getString("Id")==null||map.getString("Id").equals("")
                ||map.getString("cardId")==null||map.getString("cardId").equals("")
                ||map.getString("newloginPwd")==null||map.getString("newloginPwd").equals("")
                ||map.getString("code")==null||map.getString("code").equals("")){
            throw new RequestHandleException("参数异常！");
        }

        if(code.equals(map.getString("code"))){
            if(clientInformationService.updateEbankUserPwd(map.getString("Id"),
                    map.getString("cardId"),
                    map.getString("newloginPwd"))==0);{
                        session.removeAttribute("codeOfUpdatePwd");
                        return new NormalMassage("网银登录密码修改成功").toMap();
            }

        }

        throw new RequestHandleException("验证码错误");
    }

    /**
     * 修改银行卡密码
     * @param map
     * @return
     */
    @RequestMapping("/client/newpayPwd")
    public Map<String,Object> updateCardPwd(@RequestBody JSONObject map,HttpSession session){
        String code=(String) session.getAttribute("codeOfUpdatePwd");
        if(code==null){
            throw new RequestHandleException("未发送验证码");
        }
        //判断参数是否异常
        if(map.getString("Id")==null||map.getString("Id").equals("")
                ||map.getString("cardId")==null||map.getString("cardId").equals("")
                ||map.getString("newpayPwd")==null||map.getString("newpayPwd").equals("")
                ||map.getString("code")==null||map.getString("code").equals("")){
            throw new RequestHandleException("参数异常！");
        }
        //验证码是否正确
        if(code.equals(map.getString("code"))){
            if(clientInformationService.updateCardPwd(map.getString("Id"),
                    map.getString("cardId"),
                    map.getString("newpayPwd"))==0);{
                        session.removeAttribute("codeOfUpdatePwd");
                        return new NormalMassage("银行卡密码修改成功").toMap();
            }
        }
        throw new RequestHandleException("验证码错误！");
    }

    /**
     *柜员查询某客户交易流水
     * @how:
     * @Param
     *      map：接收前端传值
     *         type 查询类型1存款、2取款、4转账、8利息结算  0为全部
     *         endTime  结束时间
     *         startTime  开始时间
     *         cardId -查询银行卡号
     **/
    @RequestMapping("/transactionFlow/query")
    public Map<String, Object> queryTransactionFlow(@RequestBody JSONObject map){
        Timestamp startTime=null;
        Timestamp endTime=null;
        //判断map中值是否为空
        if (map.getDate("startTime")!=null){
            startTime =new Timestamp(map.getDate("startTime").getTime());
        }
        if (map.getDate("endTime")!=null){
            endTime =new Timestamp(map.getDate("endTime").getTime());
        }
        if(map.getInteger("type")==null){
            throw new RequestHandleException(1,"查询类型为空！");
        }
        if(map.getString("cardId")==null&&map.getString("cardId").equals("")){
            throw  new RequestHandleException("卡号不能为空！");
        }
        //检查时间范围是否在近三个月以内
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        timestamp= DateUtil.lastNumMonth(timestamp,3);

        if(startTime.before(timestamp)||endTime.before(timestamp)) {
            throw new RequestHandleException("只能查询近三个月内记录");
        }

        //数据验证完毕
        List<TransactionFlow> list=transactionFlowService.queryTransactionFlow(map.getString("cardId"),
                map.getInteger("type"),startTime,endTime);

        //返回前端数据 交易流水 list
        return new NormalMassage(list).toMap();
    }

    /****************************               系统              ************************/

    /**
     * 获取系统当前日期
     * @return
     */
    @RequestMapping("/systemDate")
    public Date getSystemDate(){
        //获取当前系统日期

        return  systemInformationService.getSystemDate();
    }
}
