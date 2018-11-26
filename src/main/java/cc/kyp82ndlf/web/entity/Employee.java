package cc.kyp82ndlf.web.entity;

import cc.kyp82ndlf.base.NotDatabaseField;
import cc.kyp82ndlf.base.entity.BaseEntity;
import cc.kyp82ndlf.base.security.rbac.Role;

import java.util.List;

/** 员工信息 */
public class Employee extends BaseEntity {
    public enum Daos{
        selectEmployeeIdsByParam("Employee.selectEmployeeIdsByParam", "获取员工ID"),
        selectEmployeeNormalInfoByParam("Employee.selectEmployeeNormalInfoByParam", "获取员工部分信息"),
        selectEmployeeMoreInfoByParam("Employee.selectEmployeeMoreInfoByParam", "获取员工详细信息"),
        insertEmployeeRoles("Employee.insertEmployeeRoles", "增加员工角色"),
        updateEmployeeRoles("Employee.updateEmployeeRoles", "修改员工角色"),
        ;
        public String sqlMapname;
        public String sqlRemark;
        private Daos(String name,String sqlRemark) {
            this.sqlMapname = name;
            this.sqlRemark = sqlRemark;
        }
        public String toString() { return this.sqlMapname; };
    }

    /**
     * 微信OpenId
     */
    private String openId;


    /**
     * 部门
     */
    private Integer departmentId;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 手机
     */
    private String mobile;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 微信头像
     */
    private String headerImg;

    @NotDatabaseField
    private Department department;

    @NotDatabaseField
    private List<Role> roles;

    /**
     * 角色ID
     */
    @NotDatabaseField
    private Integer roleId;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getHeaderImg() {
        return headerImg;
    }

    public void setHeaderImg(String headerImg) {
        this.headerImg = headerImg;
    }
}