package com.school.base.security;

import com.school.base.common.security.IContextService;
import com.school.base.common.security.UserInfo;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 登录用户信息服务
 * 
 * @author work
 */
@Service
public class ContextService implements IContextService {

    @Override
    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal().toString();
    }

    @Override
    public UserInfo getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getDetails() instanceof UserInfo) {
            UserInfo userInfo = (UserInfo) authentication.getDetails();
            return userInfo;
        } else {
            return null;
        }
    }

    /**
     * 填充用户登录信息
     *
     * @param userInfo
     */
    @Override
    public void setupContext(UserInfo userInfo) {
        userInfo.setName(null);
        userInfo.setDepartment(null);
        userInfo.setEmail(null);
        userInfo.setNo(null);
        userInfo.setPhone(null);
        userInfo.setType(null);
    }
}
