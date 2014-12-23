package com.school.base.common.security;

import java.io.Serializable;
import java.util.List;

/**
 * 权限接口，用于获取和判断登录用户的权限
 *
 * @author work
 */
public interface IPrivilegeProvider extends Serializable {

    /**
     * 判断访问URL是否已经被定义，若已定义则验证该URL权限，若未定义则默认所有用户均可访问该URL
     *
     * @param requestURL
     * @return
     */
    boolean isPrivilegeDefined(String requestURL) throws IllegalStateException;

    /**
     * 根据用户名返回该用户拥有的权限URL集合
     *
     * @param userName
     * @return
     */
    List<String> getPrivilegesUrlByUser(String userName) throws IllegalStateException;
    
    /**
     * 根据请求URL返回正则表达式所能匹配的权限URL集合
     * 
     * @param requestUrl
     * @return 
     */
    List<String> getPrivilegesUrlByRequestUrl(String requestUrl) throws IllegalStateException;
}
