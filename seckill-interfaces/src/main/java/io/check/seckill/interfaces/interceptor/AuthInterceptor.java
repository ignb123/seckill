package io.check.seckill.interfaces.interceptor;

import io.check.seckill.domain.code.HttpCode;
import io.check.seckill.domain.constants.SeckillConstants;
import io.check.seckill.domain.exception.SeckillException;
import io.check.seckill.infrastructure.shiro.utils.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String USER_ID = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object userIdObj = request.getAttribute(USER_ID);
        if (userIdObj != null){
            return true;
        }
        String token = request.getHeader(SeckillConstants.TOKEN_HEADER_NAME);
        if (StringUtils.isEmpty(token)){
            throw new SeckillException(HttpCode.USER_NOT_LOGIN);
        }
        Long userId = JwtUtils.getUserId(token);
        if (userId == null){
            throw new SeckillException(HttpCode.USER_NOT_LOGIN);
        }
        HttpServletRequestWrapper authRequestWrapper = new HttpServletRequestWrapper(request);
        authRequestWrapper.setAttribute(USER_ID, userId);
        return true;
    }
}
