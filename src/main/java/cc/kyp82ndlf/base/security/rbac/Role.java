package cc.kyp82ndlf.base.security.rbac;


import cc.kyp82ndlf.base.NotDatabaseField;
import cc.kyp82ndlf.base.entity.BaseEntity;

import java.util.List;

/**
 * 角色(客服，销售，电销)
 */
public class Role extends BaseEntity {
    private String name;
    private String code;
    private String description;
    @NotDatabaseField
    private List<Privilege> privileges; //拥有的权限

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

    public List<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
    }
}
