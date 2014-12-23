package com.school.base.common.web;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

/**
 * 简单URL重置过滤器
 *
 * @author work
 */
public class SimpleUrlRedirectFilter implements Filter {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private String targetUrl;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.targetUrl = filterConfig.getInitParameter("targetUrl");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.redirectStrategy.sendRedirect((HttpServletRequest) request, (HttpServletResponse) response, this.targetUrl);
    }

    @Override
    public void destroy() {
    }
}
