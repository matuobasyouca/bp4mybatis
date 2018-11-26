package cc.kyp82ndlf.base.security.rbac;

import cc.kyp82ndlf.base.entity.BaseEntity;

/**
 * 资源对应允许的操作(增，删，改，查)
 */
public class Permission extends BaseEntity {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
