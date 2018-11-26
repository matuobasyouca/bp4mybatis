package cc.kyp82ndlf.web.entity;

import cc.kyp82ndlf.base.Constant;
import cc.kyp82ndlf.base.entity.BaseEntity;
import com.zscp.master.util.ValidUtil;

/** 部门信息 */
public class Department extends BaseEntity {

    public enum Daos{
        selectDepartmentPageByParam("Department.selectDepartmentPageByParam","获取部门列表"),
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
     * 部门简称
     */
    private String name;

    /**
     * 部门描述
     */
    private String detail;

    /**
     * 部门联系方式
     */
    private String officePhone;

    /**
     * 省份
     */
    private Integer province;

    /**
     * 城市
     */
    private Integer city;

    /**
     * 区县
     */
    private Integer district;

    /**
     * 详细地址
     */
    private String address;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public Integer getProvince() {
        return province;
    }

    public void setProvince(Integer province) {
        this.province = province;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public Integer getDistrict() {
        return district;
    }

    public void setDistrict(Integer district) {
        this.district = district;
    }

    /**
     * 省份名称
     * @return
     */
    public String getProvinceDesc(){
        if(!ValidUtil.isEmpty(province)) {
            return Constant.getAreaCodesById(province).getAreaName();
        }else {
            return null;
        }
    }

    /**
     * 城市名称
     * @return
     */
    public String getCityDesc(){
        if(!ValidUtil.isEmpty(province)) {
            return Constant.getAreaCodesById(city).getAreaName();
        }else {
            return null;
        }

    }

    /**
     * 区域名称
     * @return
     */
    public String getDistrictDesc(){
        if(!ValidUtil.isEmpty(district)) {
            return Constant.getAreaCodesById(district).getAreaName();
        }else {
            return null;
        }
    }
}