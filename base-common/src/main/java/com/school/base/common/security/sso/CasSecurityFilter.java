package com.school.base.common.security.sso;

import com.school.base.common.security.IContextService;
import com.school.base.common.security.SecurityFilter;
import com.school.base.common.security.UserInfo;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author work
 */
public class CasSecurityFilter extends SecurityFilter {

    @Autowired
    private IContextService contextService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        FilterInvocation filterInvocation = new FilterInvocation(request, response, chain);
        this.invoke(filterInvocation);
    }

    private void invoke(FilterInvocation filterInvocation) throws IOException, ServletException {
        SecurityContextHolderAwareRequestWrapper wrapper = (SecurityContextHolderAwareRequestWrapper) filterInvocation.getRequest();
        HttpServletRequest request = (HttpServletRequest) wrapper.getRequest();

        //
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        //保存单点登录信息
        String username = this.contextService.getUsername();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean nameChnge = authentication == null || !(username.equals(authentication.getPrincipal().toString()));
        if (nameChnge) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, "");
            UserInfo userInfo = new UserInfo(username, "");
            userInfo.setIpAddress(request.getRemoteAddr());
            userInfo.setLoginTime(new Date());
            this.contextService.setupContext(userInfo);
            token.setDetails(userInfo);

            authentication = this.getAuthenticationManager().authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        //验证权限
        InterceptorStatusToken token = super.beforeInvocation(filterInvocation);
        try {
            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
        } finally {
            super.afterInvocation(token, null);
        }
    }
}
