package com.school.base.security;

import com.school.base.common.security.ILoginService;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 * 登录验证实现类
 *
 * @author work
 */
@Service
public class LoginService implements ILoginService {

    private static final long serialVersionUID = 1L;

    /**
     * 登录验证，验证无效登录时候抛出AuthenticationException异常
     *
     * @see UsernameNotFoundException
     * @see DisabledException
     * @see AuthenticationException
     *
     * @param username
     * @param password
     * @throws AuthenticationException
     */
    @Override
    public void login(String username, String password) throws AuthenticationException {
        //TODO:自定义验证规则
    }
}
