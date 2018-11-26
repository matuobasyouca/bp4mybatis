package cc.kyp82ndlf.base.security.rbac;

import cc.kyp82ndlf.base.entity.BaseEntity;

/**
 * 资源
 */
public class Resource extends BaseEntity {
    private String name;
    private String code;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
