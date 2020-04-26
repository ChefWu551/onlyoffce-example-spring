package com.example.onlineEditorFront.config.filter;

import com.example.onlineEditorFront.exceptions.UnauthorizedException;
import com.example.onlineEditorFront.model.UserInfo;
import com.example.onlineEditorFront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author wuyuefeng
 * @brief token过滤
 * @email 565948592@qq.com
 * @date 2020-04-24
 */
@Component
public class AccessTokenFilter implements Filter {

    @Autowired
    UserService userService;

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        ParameterRequestWrapper parameterRequestWrapper = new ParameterRequestWrapper(request);

//        String accessToken = getAccessToken(request);
        String accessToken = "45bc64ca1cae9982413a70a1c048b584";
        // todo:判空统一异常输出，目前没想到办法实现
        if (StringUtils.isEmpty(accessToken)) {
            throw new UnauthorizedException();
        }

        UserInfo userInfo;
        if (!StringUtils.isEmpty(accessToken)) {
            userInfo = userService.getUserInfo(accessToken);

            parameterRequestWrapper.addParameter("userId", userInfo.getUserId());
            parameterRequestWrapper.addParameter("userName", userInfo.getName());
            parameterRequestWrapper.addParameter("accessToken", accessToken);
        }

        filterChain.doFilter(parameterRequestWrapper, servletResponse);
    }

    @Override
    public void destroy() {

    }

    /**
     * 获取accessToken
     *
     * @param request 请求域
     * @return accessToken token
     */
    private String getAccessToken(HttpServletRequest request) {
        String accessTokenKey = "access-token";
        return request.getHeader(accessTokenKey);
    }

}
