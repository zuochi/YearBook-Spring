package com.school.base.security;

import com.school.base.common.security.IPrivilegeProvider;

import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.springframework.stereotype.Service;

/**
 * 权限提供服务实现类
 *
 * @author work
 */
@Service
public class PrivilegeProvider implements IPrivilegeProvider {

    private static final long serialVersionUID = 1L;

    /**
     * 判断访问URL是否已经被定义，若已定义(True)则验证该URL权限，若未定义(False)则默认所有用户均可访问该URL
     *
     * @param requestURL
     * @return
     */
    @Override
    public boolean isPrivilegeDefined(String requestURL) throws IllegalStateException {
        return false;
    }

    /**
     * 根据用户名返回该用户拥有的权限URL集合
     *
     * @param userName
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> getPrivilegesUrlByUser(String userName) throws IllegalStateException {
        return ListUtils.EMPTY_LIST;
    }

    /**
     * 根据请求URL返回正则表达式所能匹配的权限URL集合
     *
     * @param requestUrl
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> getPrivilegesUrlByRequestUrl(String requestUrl) throws IllegalStateException {
        return ListUtils.EMPTY_LIST;
    }
}
