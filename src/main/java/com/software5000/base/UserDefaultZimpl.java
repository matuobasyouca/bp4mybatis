package com.software5000.base;

import java.util.Set;

public class UserDefaultZimpl {

    public UserDefaultZimpl() {
    }

    public UserDefaultZimpl(String userId, String password, String loginType, String openId, BaseEntity realEntity) {
        this.userId = userId;
        this.loginType = loginType;
        this.realEntity = realEntity;
        this.password = password;
        this.openId = openId;
    }

    public UserDefaultZimpl(String userId, String password, String loginType, BaseEntity realEntity) {
        this.userId = userId;
        this.loginType = loginType;
        this.realEntity = realEntity;
        this.password = password;
    }


    private String userId;
    private String password;
    private String openId;
    private String loginType;
    private BaseEntity realEntity;
    private Set<String> roles;
    private Set<String> permissions;

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public Object getPrincipal() {
        return getUserId();
    }

    public Object getCredentials() {
        return getPassword();
    }

    @Override
    public String toString() {
        return this.getUserId();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public BaseEntity getRealEntity() {
        return realEntity;
    }

    public void setRealEntity(BaseEntity realEntity) {
        this.realEntity = realEntity;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
