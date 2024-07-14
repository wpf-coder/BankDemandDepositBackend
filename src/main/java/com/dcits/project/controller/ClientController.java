package com.dcits.project.controller;

import com.alibaba.fastjson.JSONObject;
import com.dcits.project.aop.Authority;
import com.dcits.project.aop.Role;
import com.dcits.project.common.DateUtil;
import com.dcits.project.common.NormalMassage;
import com.dcits.project.dto.TransferTransaction;
import com.dcits.project.exception.RequestHandleException;
import com.dcits.project.pojo.Card;
import com.dcits.project.pojo.TransactionFlow;
import com.dcits.project.pojo.User;
import com.dcits.project.service.BranchService;
import com.dcits.project.service.CardService;
import com.dcits.project.service.ClientInformationService;
import com.dcits.project.service.TransactionFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/client")
@Authority(role = Role.CLIENT)
public class ClientController {
    @Autowired
    private CardService cardService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private TransactionFlowService transactionFlowService;
    @Autowired
    ClientInformationService clientInformationService;
    /**
     * 用户转账
     * @how:
     * @return:
     *       java.util.Map<java.lang.String,java.lang.Object>
    **/
    @RequestMapping("/transfer")
    public Map<String,Object> transfer(@RequestBody TransferTransaction transferTransaction){
        if(!transferTransaction.checkNotNull())
            throw new RequestHandleException("参数不完整");
        if(!Card.validateCardId(transferTransaction.getPayerCardId()))
            throw new RequestHandleException("付款人银行卡格式不正确");
        if(!Card.validateCardId(transferTransaction.getPayeeCardId()))
            throw new RequestHandleException("收款人银行卡格式不正确");
        if(transferTransaction.getAmount().compareTo(new BigDecimal(0)) == 1)
            throw new RequestHandleException("转账额度必须大于0");
        if(transferTransaction.getPayerCardId().equals(transferTransaction.getPayeeCardId()))
            throw new RequestHandleException("收款账户与汇款账户不能是一个。");

        cardService.transfer(transferTransaction.getPayerCardId(),
                transferTransaction.getPayeeCardId(),
                transferTransaction.getAmount(),
                transferTransaction.getPayerCardPassword(),
                transferTransaction.getPayeeName());

        NormalMassage massage = new NormalMassage();
        return massage.toMap();
    }


    /**
     *查询交易流水
     * @how:
     * @Param
     *      map：接收前端传值
     *         type 查询类型1存款、2取款、4转账、8利息结算  0为全部
     *         endTime  结束时间
     *         startTime  开始时间
     * @return:
     *
     **/
    @RequestMapping("/transactionFlow/query")
    public Map<String, Object> queryTransactionFlow(@RequestBody JSONObject map, HttpSession session){
        Timestamp startTime=null;
        Timestamp endTime=null;
        //判断map中值是否为空
        if(map.getInteger("type")==null){
            throw new RequestHandleException(1,"查询类型为空！");
        }
        if (map.getDate("startTime")!=null){
            startTime =new Timestamp(map.getDate("startTime").getTime());
        }
        if (map.getDate("endTime")!=null){
            endTime =new Timestamp(map.getDate("endTime").getTime());
        }


        //检查时间范围是否在近三个月以内
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        timestamp= DateUtil.lastNumMonth(timestamp,3);

        if(startTime.before(timestamp)||endTime.before(timestamp)) {
            throw new RequestHandleException("只能查询近三个月内记录");
        }

        //数据验证完毕
        User user=(User)session.getAttribute("user");
        //查找user对应卡号
        String cardId=cardService.queryCardInformation(user).getId();
        List<TransactionFlow> list=transactionFlowService.queryTransactionFlow(cardId,
                map.getInteger("type"),startTime,endTime);

        //返回前端数据
        return new NormalMassage(list).toMap();
    }


     /**
      * 获取用户 user以及card信息
     **/
    @RequestMapping("/info")
    public Map<String, Object> queryTransactionFlow(HttpSession session){

        User user=(User) session.getAttribute("user");
        //根据身份证号码查询信息
        return new NormalMassage(
                "查询成功",
                NormalMassage.NORMAL_STATE,
                clientInformationService.infoQuery(user.getId())).toMap();
    }

    /**
     * 重置初始登录密码
     * @param map
     *          resetpwd  新密码
     * @param session
     * @return
     */
    @RequestMapping("/reset")
    public Map<String,Object> resetEbankPwd(@RequestBody JSONObject map, HttpSession session){
        User user = (User) session.getAttribute("user");
        //检测参数 resetpwd
        if(map.getString("resetpwd")==null||map.getString("resetpwd").equals("")){
            throw new RequestHandleException("参数异常！");
        }
        //重置密码
        clientInformationService.resetEbankPwd(user.getId(),map.getString("resetpwd"));
        return new NormalMassage("密码更新成功！").toMap();
    }
}
