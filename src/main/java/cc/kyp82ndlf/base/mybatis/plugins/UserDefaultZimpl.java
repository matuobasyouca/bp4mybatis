package cc.kyp82ndlf.base.mybatis.plugins;

import cc.kyp82ndlf.base.entity.BaseEntity;

import java.util.Set;

public class UserDefaultZimpl {

    public UserDefaultZimpl() {
    }

    public UserDefaultZimpl(String openId, Integer userId, BaseEntity realEntity) {
        this.openId = openId;
        this.userId = userId;
        this.realEntity = realEntity;
    }

    private String openId;
    private Integer userId;
    private BaseEntity realEntity;
    private Set<String> roles;
    private Set<String> permissions;

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Object getPrincipal() {
        return getOpenId();
    }

    public Object getCredentials() {
        return getRealEntity();
    }

    public String toString() {
        return this.getOpenId();
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
}
