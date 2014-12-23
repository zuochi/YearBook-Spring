package com.school.base.common.security;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * 保存登录用户详细信息(可拓展)
 *
 * @author work
 */
public class UserInfo extends User {

    private static final long serialVersionUID = -6869488151836596942L;
    //编号(例如工号)
    private String no;
    //姓名
    private String name;
    //IP
    private String ipAddress;
    //登录时间
    private Date loginTime;
    //部门
    private String department;
    //联系电话
    private String phone;
    //电子邮箱
    private String email;
    //用户类型
    private String type;

    @SuppressWarnings("unchecked")
    public UserInfo(String username, String password) {
        super(username, password, Collections.EMPTY_LIST);
    }

    public UserInfo(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public UserInfo(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    void copy(UserInfo userInfo) {
        BeanUtils.copyProperties(userInfo, this);
    }

    public Map<String, Object> asMap() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        String content = mapper.writeValueAsString(this);
        return mapper.readValue(content, new TypeReference<Map<String, Object>>() {
        });
    }
}
