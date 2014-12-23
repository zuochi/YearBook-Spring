package com.school.base.common.security;

import java.io.Serializable;
import org.springframework.security.core.AuthenticationException;

/**
 * 登录服务接口，用于验证登录
 *
 * @author work
 */
public interface ILoginService extends Serializable {

    /**
     * 登录验证服务，登录失败时抛出验证异常
     *
     * @param username
     * @param password
     * @throws AuthenticationException
     */
    void login(String username, String password) throws AuthenticationException;
}
