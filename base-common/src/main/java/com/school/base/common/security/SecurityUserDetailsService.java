package com.school.base.common.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 获取登录用户权限并转成Spring识别的格式
 *
 * @author work
 */
@Service
public class SecurityUserDetailsService implements UserDetailsService, AuthenticationManager, Serializable {

    private static final long serialVersionUID = 2528244446844129012L;
    @Autowired
    private IPrivilegeProvider privilegeProvider;

    @Override
    public UserDetails loadUserByUsername(final String userName) throws UsernameNotFoundException, DataAccessException {
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        List<String> privilegs = this.privilegeProvider.getPrivilegesUrlByUser(userName);
        for (String privilege : privilegs) {
            GrantedAuthority authority = new SimpleGrantedAuthority(privilege);
            list.add(authority);
        }
        UserInfo user = new UserInfo(userName, "", list);
        return user;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserInfo userInfo = (UserInfo) this.loadUserByUsername(authentication.getName());
        userInfo.copy(((UserInfo) authentication.getDetails()));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userInfo.getUsername(), userInfo.getPassword(), userInfo.getAuthorities());
        authenticationToken.setDetails(userInfo);
        return authenticationToken;
    }
}
