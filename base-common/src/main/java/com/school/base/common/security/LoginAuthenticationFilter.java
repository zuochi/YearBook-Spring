package com.school.base.common.security;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;

/**
 * Spring触发的登录验证拦截器
 *
 * @author work
 */
@Service
public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private ILoginService loginService;
    @Autowired
    private IContextService contextService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        String username = this.obtainUsername(request).trim();
        String password = this.obtainPassword(request).trim();

        try {
            this.loginService.login(username, password);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    username, password);
            UserInfo details = new UserInfo(username, "");
            details.setIpAddress(request.getRemoteAddr());
            details.setLoginTime(new Date());
            this.contextService.setupContext(details);
            authenticationToken.setDetails(details);
            Authentication authentication = super.getAuthenticationManager().authenticate(authenticationToken);

            return authentication;
        } catch (AuthenticationException e) {
            throw e;
        }
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter("username");
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter("password");
    }
}
