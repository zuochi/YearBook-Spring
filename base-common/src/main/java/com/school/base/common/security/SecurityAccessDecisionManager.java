package com.school.base.common.security;

import java.util.Collection;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Spring触发的权限验证器
 *
 * @author work
 */
public class SecurityAccessDecisionManager implements AccessDecisionManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IPrivilegeProvider privilegeProvider;

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> attributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        if (attributes == null || attributes.isEmpty()) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (ConfigAttribute attribute : attributes) {
            builder.append(attribute.getAttribute()).append(" ");
        }

        this.logger.debug("检查权限：" + builder.toString());

        if (authentication == null) {
            this.logger.warn("访问受限：" + builder.toString());

            throw new AccessDeniedException("访问受限：" + builder.toString());
        }
        // 所请求的资源拥有的权限(一个资源对多个权限)
        Iterator<ConfigAttribute> iterator = attributes.iterator();
        while (iterator.hasNext()) {
            ConfigAttribute configAttribute = iterator.next();
            // 访问所请求资源所需要的权限
            String requestURL = configAttribute.getAttribute();
            // 如果未定义的URL，所有登录用户默认拥有权限
            if (!this.privilegeProvider.isPrivilegeDefined(requestURL)) {
                this.logger.debug("未定义权限的URL，默认所有登录用户拥有访问权限：" + builder.toString());
                return;
            }
            // 用户所拥有的权限authentication
            for (GrantedAuthority ga : authentication.getAuthorities()) {
                if (requestURL.equals(ga.getAuthority())) {
                    return;
                }
            }
        }

        // 没有权限
        this.logger.warn("访问受限：" + builder.toString());
        throw new AccessDeniedException("访问受限：" + builder.toString());
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> cls) {
        return true;
    }
}
