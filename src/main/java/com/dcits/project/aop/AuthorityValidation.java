package com.dcits.project.aop;


import com.dcits.project.common.Util;
import com.dcits.project.pojo.Teller;
import com.dcits.project.pojo.User;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class AuthorityValidation implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean judge = true;

        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Authority authority = null;
            authority = handlerMethod.getMethod().getDeclaringClass().getAnnotation(Authority.class);
            if(authority == null)
                authority = handlerMethod.getMethod().getAnnotation(Authority.class);

            //若controller有Authority注解，则需要进行登录状态检测，
            if(authority != null){
                Object user = request.getSession().getAttribute("user");
                //若未登录，则 进行 拦截，并且 请求重定向 前端的冷路
                if(user == null){
                    System.out.println("AuthorityValidation redirect request to "
                            + request.getHeader("Referer")+" , because No logged!");
                    //下面这句话会让vue进行直接代理跳转。
                    //response.sendRedirect(request.getHeader("Referer"));
                    response.setHeader("redirect_url",request.getHeader("Referer"));
                    response.setStatus(302);

                    judge = false;
                }else{
                    //若 请求的身份不满足 接口的对角色的要求，则拦截，且不返回任何信息。
                    switch (authority.role()){
                        case CLIENT: {
                            if (!(user instanceof User)) {
                                judge = false;
                            }
                            break;
                        }
                        case TELLER:{
                            if(!(user instanceof Teller))
                                judge = false;
                            break;
                        }
                    }
                }
            }
        }

        return judge;
    }
}
