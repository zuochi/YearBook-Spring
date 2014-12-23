package com.school.base.common.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

/**
 * 资源访问请求并转成Spring识别的格式
 *
 * @author work
 */
public class SecurityInvokeMetadataSource implements FilterInvocationSecurityMetadataSource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IPrivilegeProvider privilegeProvider;

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return ListUtils.EMPTY_LIST;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        FilterInvocation filterInvocation = (FilterInvocation) object;
        String requestUrl = filterInvocation.getRequestUrl();
        List<ConfigAttribute> list = new ArrayList<ConfigAttribute>();

        this.logger.debug("访问请求URL : " + requestUrl);

        //根据正则表达式查看是否是可变URL
        List<String> privilegesUrl = this.privilegeProvider.getPrivilegesUrlByRequestUrl(requestUrl);
        if (privilegesUrl.isEmpty()) {
            ConfigAttribute attribute = new SecurityConfig(requestUrl);
            this.logger.debug("权限请求URL : " + attribute.getAttribute());
            list.add(attribute);
        } else {
            for (String privilegeUrl : privilegesUrl) {
                ConfigAttribute attribute = new SecurityConfig(privilegeUrl);
                this.logger.debug("权限请求URL : " + attribute.getAttribute());
                list.add(attribute);
            }
        }

        return list;
    }

    @Override
    public boolean supports(Class<?> cls) {
        return true;
    }
}
