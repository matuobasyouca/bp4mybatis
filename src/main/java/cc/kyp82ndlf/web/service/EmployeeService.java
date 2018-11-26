package cc.kyp82ndlf.web.service;


import cc.kyp82ndlf.base.BaseDao;
import cc.kyp82ndlf.base.Constant;
import cc.kyp82ndlf.base.security.rbac.Role;
import cc.kyp82ndlf.web.entity.Employee;
import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {
    private Log log = LogFactory.getLog(EmployeeService.class);

    private static List<Employee> employees;

    @Resource
    private BaseDao baseDao;

    //=========================================内存操作=============================================================================
    public static void initAllEmployee(List employees) {
        EmployeeService.employees = employees;
    }

    /**
     * 从内存中获取所有员工数据
     * @return
     */
    public static List<Employee> getAllEmployeesFromMemory() {
        return employees;
    }

    public static Employee getEmployeeByEmployeeIdFromMem(Integer employeeId) {
        for (Employee employee : employees) {
            if (employeeId != null && employee.getId().equals(employeeId)) {
                return employee;
            }
        }
        return null;
    }
    //======================================================================================================================

    //<editor-fold desc="insert (增)">
    /* ----------------------------------------------------------- insert (增) start ----------------------------------------------------------------*/

    /**
     * 创建员工
     * @param employee
     * @return
     * @throws SQLException
     */
    public Employee insertEmployee(Employee employee) throws SQLException {

        //新增员工信息
        employee = baseDao.insertEntity(employee);

        //新建角色
        Map param = new HashMap();
        param.put("employeeId", employee.getId());
        param.put("roleId", employee.getRoleId());
        baseDao.insert(Employee.Daos.insertEmployeeRoles.sqlMapname, param);

        //新增后更新内存
        EmployeeService.initAllEmployee(this.selectAllEmployeesWithDepartment());

        param = new HashMap<String, Object>();
        param.put("id", employee.getId());
        return this.selectEmployeeMoreInfoByParam(param);
    }

    /* ----------------------------------------------------------- insert (增) end ----------------------------------------------------- -----------*/
    //</editor-fold>


    //<editor-fold desc="delete (删)">
    /* ----------------------------------------------------------- delete (删) start ----------------------------------------------------------------*/
    
    /* ----------------------------------------------------------- delete (删) end ----------------------------------------------------- -----------*/
    //</editor-fold>


    //<editor-fold desc="update (改)">
    /* ----------------------------------------------------------- update (改) start ----------------------------------------------------------------*/

    /**
     * 编辑员工
     * @param employee
     * @throws SQLException
     */
    public void updateEmployee(Employee employee, boolean isSupportBlank) throws SQLException {
        if(employee.getId() != null) {
            //更新员工信息
            baseDao.updateEntityOnlyHaveValue(employee, isSupportBlank);

            //判断是否修改roleId
            if(employee.getRoleId() != null) {
                Map rolesMap = new HashMap();
                rolesMap.put("roleId", employee.getRoleId());
                rolesMap.put("employeeId", employee.getId());
                baseDao.update(Employee.Daos.updateEmployeeRoles.sqlMapname, rolesMap);
            }

            //新增后更新内存
            EmployeeService.initAllEmployee(this.selectAllEmployeesWithDepartment());
        }
    }

    /* ----------------------------------------------------------- update (改) end ----------------------------------------------------- -----------*/
    //</editor-fold>


    //<editor-fold desc="select (查)">
    /* ----------------------------------------------------------- select (查) start ----------------------------------------------------------------*/

    /**
     * 获取角色列表
     * @return
     * @throws SQLException
     */
    public List<Role> selectRoleList() throws SQLException {
        return baseDao.selectEntity(new Role());
    }

    /**
     * 查询员工普通信息
     * @param param
     * @return
     * @throws SQLException
     */
    public Employee selectEmployeeNormalInfoByParam(Map<String, Object> param) throws SQLException {
        return (Employee)baseDao.selectObject(Employee.Daos.selectEmployeeNormalInfoByParam.sqlMapname, param);
    }

    /**
     * 查询员工普通信息列表
     * @param param
     * @return
     * @throws SQLException
     */
    public List<Employee> selectEmployeeListNormalInfoByParam(Map<String, Object> param) throws SQLException {
        return (List<Employee>)baseDao.selectList(Employee.Daos.selectEmployeeNormalInfoByParam.sqlMapname, param);
    }

    /**
     * 根据条件查询在职员工信息（包含部门，权限等）
     * @param param
     * @return
     * @throws SQLException
     */
    public Employee selectEmployeeMoreInfoByParam(Map<String, Object> param) throws SQLException {
        return (Employee)baseDao.selectObject(Employee.Daos.selectEmployeeMoreInfoByParam.sqlMapname, param);
    }

    /**
     * 根据条件查询在职员工信息 列表（包含部门，权限等）
     * @param param
     * @return
     * @throws SQLException
     */
    public List<Employee> selectEmployeeListByParam(Map<String, Object> param) throws SQLException {
        return (List<Employee>)baseDao.selectList(Employee.Daos.selectEmployeeMoreInfoByParam.sqlMapname, param);
    }

    /**
     * 获取员工列表
     * @param param
     * @return
     * @throws SQLException
     */
    public PageInfo<Employee> selectEmployeePageByParam(Map<String, Object> param) throws SQLException {

        //获取员工列表ID
        PageInfo pageInfo = baseDao.selectListByPage(Employee.Daos.selectEmployeeIdsByParam.sqlMapname
                                                            ,param
                                                            ,Integer.valueOf(param.getOrDefault("startPage", 1).toString())
                                                            ,Integer.valueOf(param.getOrDefault("pageSize", 10).toString())
                                                            ," e.state asc, e.createTime desc");

        if(pageInfo.getList().size() > 0) {
            Map<String, Object> mParam = new HashMap<String, Object>();
            mParam.put("ids", pageInfo.getList());
            mParam.put("orderBy", " e.state asc, e.createTime desc");
            pageInfo.setList(baseDao.selectList(Employee.Daos.selectEmployeeMoreInfoByParam.sqlMapname, mParam));
        }

        return pageInfo;
    }

    /**
     * 获取全部的员工信息
     * @return
     * @throws SQLException
     */
    public List<Employee> selectAllEmployeesWithDepartment() throws SQLException {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("state", Constant.EmployeeState.EMPLOYEE_VALID.codeName);
        return (List<Employee>) baseDao.selectList(Employee.Daos.selectEmployeeMoreInfoByParam.sqlMapname);
    }

    /* ----------------------------------------------------------- select (查) end ----------------------------------------------------- -----------*/
    //</editor-fold>


}