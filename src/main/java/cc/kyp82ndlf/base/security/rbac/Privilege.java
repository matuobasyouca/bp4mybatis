package cc.kyp82ndlf.base.security.rbac;


import cc.kyp82ndlf.base.NotDatabaseField;
import cc.kyp82ndlf.base.entity.BaseEntity;

/**
 * 权限，记录资源对应的操作
 */
public class Privilege extends BaseEntity {
    @NotDatabaseField
    private Role role;
    private Integer roleId;
    @NotDatabaseField
    private Resource resource;//资源
    private Integer resourceId;
    //操作的集合,用逗号隔开
    private String permissionValues;
    //描述
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public String getPermissionValues() {
        return permissionValues;
    }

    public void setPermissionValues(String permissionValues) {
        this.permissionValues = permissionValues;
    }
}
