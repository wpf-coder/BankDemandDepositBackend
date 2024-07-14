package com.dcits.project.config;

import com.dcits.project.mapper.EbankUserMapper;
import com.dcits.project.mapper.UserMapper;
import com.dcits.project.pojo.EbankUser;
import com.dcits.project.pojo.User;
import com.dcits.project.service.UserCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.*;
import java.util.HashSet;

@WebListener
@Component
public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener {
    public static final Logger logger= LoggerFactory.getLogger(SessionListener.class);


    @Autowired
    private UserCommonService userCommonService;

    /**
     * session过期后，若session内有user信息，将其ebank的登录状态更新
     * @how:
     *      目前仅是简单的更新，事务也没管理，不一定能更新成功；需要进行
     *      一定的控制。
     * @return:
     *       void
     **/
    @Override
    public void sessionDestroyed(HttpSessionEvent event) throws ClassCastException {
        HttpSession session = event.getSession();

        //真正的业务逻辑。
        Object object = session.getAttribute("user");
        //如果有用户信息
        if(object != null){
            //若是用户登录，则更新其网上银行状态。
            if (object instanceof User) {
                userCommonService.userLogout((User)object);
            }
        }

//        ServletContext application = session.getServletContext();
//        HashSet sessions = (HashSet) application.getAttribute("sessions");
//        // 销毁的session均从HashSet集中移除
//        sessions.remove(session);
    }


//    @Override
//    public void  attributeAdded(HttpSessionBindingEvent httpSessionBindingEvent) {
//        logger.info("--attributeAdded--");
//        HttpSession session=httpSessionBindingEvent.getSession();
//        logger.info("key----:"+httpSessionBindingEvent.getName());
//        logger.info("value---:"+httpSessionBindingEvent.getValue());
//
//    }

//    @Override
//    public void attributeRemoved(HttpSessionBindingEvent httpSessionBindingEvent) {
//        logger.info("--attributeRemoved--");
//    }
//
    @Override
    public void attributeReplaced(HttpSessionBindingEvent httpSessionBindingEvent) {
        logger.info("--attributeReplaced--");

        if(httpSessionBindingEvent.getName().equals("user")){
            Object object = httpSessionBindingEvent.getValue();
            if (object instanceof User){
                userCommonService.userLogout((User)object);
            }
        }
    }

//    @Override
//    public void sessionCreated(HttpSessionEvent event) {
//        logger.info("---sessionCreated----");
//        HttpSession session = event.getSession();
//        ServletContext application = session.getServletContext();
//        // 在application范围由一个HashSet集保存所有的session
//        HashSet sessions = (HashSet) application.getAttribute("sessions");
//        if (sessions == null) {
//            sessions = new HashSet();
//            application.setAttribute("sessions", sessions);
//        }
//        // 新创建的session均添加到HashSet集中
//        sessions.add(session);
//        // 可以在别处从application范围中取出sessions集合
//        // 然后使用sessions.size()获取当前活动的session数，即为“在线人数”
//    }




}
