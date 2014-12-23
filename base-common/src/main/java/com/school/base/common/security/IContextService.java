package com.school.base.common.security;

/**
 * @author work
 */
public interface IContextService {

    String getUsername();

    UserInfo getUserInfo();

    void setupContext(UserInfo details);
}
