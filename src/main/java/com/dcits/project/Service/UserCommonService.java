package com.dcits.project.service;

import com.dcits.project.common.Util;
import com.dcits.project.exception.RequestHandleException;
import com.dcits.project.mapper.EbankUserMapper;
import com.dcits.project.mapper.TellerMapper;
import com.dcits.project.mapper.UserMapper;
import com.dcits.project.pojo.EbankUser;
import com.dcits.project.pojo.Teller;
import com.dcits.project.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserCommonService {
    @Autowired
    TellerMapper tellerMapper;

    /**
     * 柜员登录
     * @how:
     *      检验柜员id是否存在，密码是否匹配
     * @Param
     *      id：柜员id
     *      pwd：柜员登录密码
     * @return:
     *      1：登录失败
     *      0：登录成功
     **/
    @Transactional(rollbackFor = RuntimeException.class,isolation = Isolation.REPEATABLE_READ)
    public Teller tellerLogin(String id, String pwd) {
        Teller teller=tellerMapper.selectByPrimaryKey(id);
        /**
         * md5加密处理秘密码
         */
        pwd= Util.md5(pwd);
        if(teller==null||!teller.getPassword().equals(pwd)){
            throw new RequestHandleException(1,"账号或密码错误");
        }
        return teller;
    }

    /**************************************客户登录区域**************************/
    @Autowired
    EbankUserMapper ebankUserMapper;
    @Autowired
    UserMapper userMapper;

    /**
     * 验证账号、密码
     * @how:
     * @Param
     *      eBankId:网银账号，即身份证号码
     *      pwd：网银登录密码
     * @return:
     *      1：登录失败
     *      0：登录成功
     **/
    @Transactional(rollbackFor = RuntimeException.class,isolation = Isolation.REPEATABLE_READ)
    public User userIdLogin(String eBankId, String pwd,User sessionUser) {

        User user=userMapper.selectByPrimaryKey(eBankId);
        EbankUser ebankUser=ebankUserMapper.selectByPrimaryKey(eBankId);

        if(ebankUser==null||user.getEbankState()==User.NON_OPEN_EBANK_STATE||user==null){
            throw new RequestHandleException(1,"未开通网银,请到线下网点开通!");
        }
        if(ebankUser.getState()==1&&!user.equals(sessionUser)){
            throw new RequestHandleException(1,"其他人已登录该账号，请注意账号安全");
        }
        if(!ebankUser.getPassword().equals(Util.md5(pwd))){
            throw new RequestHandleException(1,"账号或密码错误!");
        }

        //更新手机号对应网银账户登录状态
        ebankUser.setState(EbankUser.INLINE_STATE);
        ebankUserMapper.updateByPrimaryKey(ebankUser);

        return user;

    }

    /**
     * 手机号登录。
     * @how:
     * @Param
     *      phone: 手机号
     *      code：用户输入的验证码
     *      valicateCode：验证的验证码
     * @return:
     *         1：登录失败
     *         0：登录成功
     **/
    @Transactional(rollbackFor = RuntimeException.class,isolation = Isolation.REPEATABLE_READ)
    public User userPhoneLogin(String phone,String code, String valicateCode,User sessionUser) {
        User user=userMapper.selectByPhone(phone).get(0);
        System.out.println(user);
        //手机号未找到或者手机号对应开户信息并为开通网银
        if(user==null||user.getEbankState()==User.NON_OPEN_EBANK_STATE){
            throw new RequestHandleException(1,"手机号未开通网银!");
        }
        //手机号对应账户已开通网银，则检验 验证码valicateCode是否正确
        if(!code.equals(valicateCode)){
            throw new RequestHandleException(1,"验证码输入错误!");
        }

        //更新手机号对应网银账户登录状态
        EbankUser ebankUser=ebankUserMapper.selectByPrimaryKey(user.getId());
        if(ebankUser.getState()==1&&!user.equals(sessionUser)){
            // TODO 重复登录问题
            throw new RequestHandleException(1,"其他人已登录该账号，请注意账号安全");
            //return null;
        }
        ebankUser.setState(EbankUser.INLINE_STATE);
        ebankUserMapper.updateByPrimaryKey(ebankUser);

        return userMapper.selectByPrimaryKey(ebankUser.getId());
    }

    /**
     * 用户退出登录
     * @param user 用来获取网银账户的身份信息
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class,isolation = Isolation.REPEATABLE_READ)
    public int userLogout(User user) {
        if (user!=null&&user.getId()!=null&&!user.getId().equals("")){
            EbankUser ebankUser=ebankUserMapper.selectByPrimaryKey(user.getId());
            ebankUser.setState(EbankUser.OFFLINE_STATE);
            ebankUserMapper.updateByPrimaryKey(ebankUser);
            return 0;
        }
        return 1;
    }

}
