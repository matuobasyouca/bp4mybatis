package cc.kyp82ndlf.base;

import cc.kyp82ndlf.base.entity.AreaCode;
import cc.kyp82ndlf.base.entity.SystemCode;
import com.zscp.master.util.ValidUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 全局变量名称定义类
 */
public class Constant {
    private static Log log = LogFactory.getLog(Constant.class);

    //***********************（start）需要从数据库获取，此处只需要记录对应的key**************************************************

    /**
     * 实体名称：无
     * 字段：无
     * 说明：基础状态
     */
    public enum ConfigCode {
        DEALER_CAR_RAM_COUNT("dealerCarRamCount","存放在内存的车源条数"),
        DEALER_CAR_EFFECT_DAY("dealerCarEffectDay","车源有效天数"),
        WANT_BUYING_OVER_DAY("wantBuyOverDay","找车过期天数"),
        AUCTION_RECORD_INSERT_TIMES("auctionRecordInserTimes","拍卖出价次数限制"),
        CARCOLOR_TYPE("CarColorType", "车辆颜色"),
        CustomerType("CustomerType","商家类型"),
        PUBLISH_LIMIT_COUNT("PublishLimitCount","商家上架车源数量"),
        DEALER_GUIDE_ADD_DAY("DealerGuideAddDay","商家引导推迟天数"),
        ;

        public String codeName; // 变量名称
        public String desc; // 描述

        ConfigCode(String codeName, String desc) {
            this.codeName = codeName;
            this.desc = desc;
        }
    }


    //***********************（end）需要从数据库获取，此处只需要记录对应的key**************************************************

    //***********************（start）不需要从数据库获取**************************************************

    /**
     * 实体名称：无
     * 字段：无
     * 说明：基础状态
     */
    public enum StateCode {
        JWT_SHARED_KEY("CJY@4009945888", "jwt 盐值"),

        NOT_AUTHENTICATION("ZS004001", "没有权限"),
        LOGIN_SUCCESS("ZS001000", "登陆成功"),
        REGISTER_SUCCESS("ZS001001", "注册成功"),
        LOGIN_FAIL("ZS002000", "用户名或密码错误"),
        VAILD_CODE_ERROR("ZS002001", "验证码错误"),
        NO_USER_ERROR("ZS002002", "不存在该用户"),
        CODE_HAVE_USE_ERR("ZS002003", "CODE已经被使用"),
        USER_INFO_NO_COMM("ZS002004", "用户信息不完整"),
        SELECT_PACKAGE("ZS002005", "请选择套餐再购买"),
        MONEY_ERROR("ZS002006", "非法订单"),
        SUCCESS("ZS021000", "操作成功"),
        ERROR("ZS021001", "操作失败"),
        JSON_CHANGE_ERROR("ZS022001", "JSON转换失败"),
        CODE_EMPTY_ERR("ZS022002","传入的code为空！"),
        IMG_ADD_ERROR("ZS022003", "图片添加失败"),
        GET_MEDIA_IMG_ERROR("ZS022005", "上传失败，请重新上传"),
        PHOTO_TASK_STATE_ERROR("ZS022006", "拍车任务已完成或无效！"),
        NAME_EXIT_ERROR("ZS022007", "套餐名称已存在"),
        BALANCE_PAY_ERROR("ZS022008", "您的余额不足，请先充值！"),

        /***车商相关 start***/
        DEALER_UNBIND_DATE_ERR("ZS022101", "该用户下套餐还未过期，不可解绑！"),
        DEALER_UNBIND_MONEY_ERR("ZS022102", "该用户下还有余额，不可解绑！"),
        CUSTOMER_DELETE_DEALER_ERR("ZS022103", "该车商下有绑定微信，不可删除！"),
        CUSTOMER_INSERT_NAME_ERR("ZS022104", "该车商已存在！"),
        CUSTOMER_INSERT_MOBILE_ERR("ZS022105", "该手机号被已绑定！"),
        AUCTION_INSERT_VINCODE_ERR("ZS022106", "车架号已在拍卖中，无法新建！"),
        AUCTION_RECORD_INSERT_ERR("ZS022107", "您已出价两次，无法再出价！"),
        AUCTION_STATE_INSERT_ERR("ZS022108", "拍卖已结束，无法出价！"),
        /***车商相关 end***/

        /***维保记录 start***/
        REPORT_LOADING("ZS022601", "报告查询中。"),
        REPORT_ERROR("ZS022602", "该车架号不支持查询。"),
        DEALER_FOR_MAINTAIN_DETAIL_ERR("ZS022603", "查询失败，该车架号无法查询或您未购买维保！"),
        /***维保记录 end***/

        /***员工相关 start***/
        EMPLOYEE_BIND_OPENID_ERR("ZS022701", "该微信号已绑定员工信息！"),
        /***员工相关 end***/

        ;

        public String codeName; // 变量名称
        public String desc; // 描述

        StateCode(String codeName, String desc) {
            this.codeName = codeName;
            this.desc = desc;
        }
    }

    /**
     * 实体名称：
     * 字段：
     * 说明：公共
     */
    public enum CommonState {

        YES(1, "是"),
        NO(2, "否"),;

        public Integer codeName; // 变量名称
        public String desc; // 描述
        public String tableName = ""; // 实体名称
        public String colName = ""; // 字段名称
        public String colDesc = "公共"; // 字段说明

        CommonState(Integer codeName, String desc) {
            this.codeName = codeName;
            this.desc = desc;
        }
    }

    /**
     * 实体名称：Employee
     * 字段：state
     * 说明：在职状态
     */
    public enum EmployeeState {
        EMPLOYEE_VALID(1, "在职"),
        EMPLOYEE_INVALID(2, "离职"),;

        public Integer codeName; // 变量名称
        public String desc; // 描述
        public String tableName = "Employee"; // 实体名称
        public String colName = "state"; // 字段名称
        public String colDesc = "部门类型"; // 字段说明

        EmployeeState(Integer codeName, String desc) {
            this.codeName = codeName;
            this.desc = desc;
        }
    }

    /**
     * 实体名称：Role
     * 字段：code
     * 说明：角色代码
     */
    public enum RoleName {
        ROLE_NAME_SALESMAN("SalesMan", "销售"),
        ROLE_NAME_SALESMAN_KEEPER("SalesManKeeper", "销售主管"),
        ROLE_NAME_SUPERMANAGER("Admin", "超级管理员"),
        ROLE_INVENTORY_MAN("inventoryMan", "盘库"),
        ;

        public String codeName; // 变量名称
        public String desc; // 描述
        public String tableName = "Role"; // 实体名称
        public String colName = "code"; // 字段名称
        public String colDesc = "角色代码"; // 字段说明

        RoleName(String codeName, String desc) {
            this.codeName = codeName;
            this.desc = desc;
        }
    }


    //***********************（end）不需要从数据库获取**************************************************


    //=============================基础变量开始======================================

    /**
     * key为枚举类名，value是一个字符串数组列表:[值，描述]
     */
    public static Map<String, List<String[]>> enums = initEnumMap();

    /**
     * key（为枚举类名，codeName），value是描述字符串desc
     */
    public static Map<String, String> enumValues;

    public static String sortCode = "sortCode";
    private static Map<String, Object> codes;

    private static List<AreaCode> areaCodes;
    private static long lastFreshTimestamp = 0L;

    //=============================基础变量结束======================================

    //=============================初始化开始======================================

    /**
     * 初始化SystemCode和客户来源
     *
     * @param codes
     */
    public static void initCodes(Map codes) {
        Constant.codes = codes;
        lastFreshTimestamp = new Date().getTime();
    }

    /**
     * 初始化枚举字段
     *
     * @return
     */
    private static Map<String, List<String[]>> initEnumMap() {
        Map<String, List<String[]>> enumsTmp = new HashMap();
        try {
            enumValues = new HashMap();
            Class<?> cls = Class.forName("cc.kyp82ndlf.base.Constant");
            for (Class sEnum : cls.getDeclaredClasses()) {
                if (sEnum.isEnum()) {
                    List<String[]> enumOptions = new ArrayList<>();
                    for (Object e : sEnum.getEnumConstants()) {
                        enumOptions.add(new String[]{e.getClass().getDeclaredField("codeName").get(e).toString(), e.getClass().getDeclaredField("desc").get(e).toString()});
                        enumValues.put(sEnum.getSimpleName() + "," + e.getClass().getDeclaredField("codeName").get(e).toString(), e.getClass().getDeclaredField("desc").get(e).toString());
                    }
                    enumsTmp.put(sEnum.getSimpleName(), enumOptions);
                }
            }
        } catch (Exception e) {
            log.error(" init Enum map error! ", e);
        }
        return enumsTmp;
    }

    /**
     * 初始化地区
     *
     * @param areCodes
     */
    public static void initAreaCode(List areCodes) {
        Constant.areaCodes = areCodes;
        lastFreshTimestamp = new Date().getTime();
    }
    //=============================初始化结束======================================

    //-----------------------------地区开始-----------------------------------------

    public static List<AreaCode> getAreaCodesByParentId(Integer parentId) {
        return areaCodes.stream().filter(areaCode -> areaCode.getParentId() != null && areaCode.getParentId().equals(parentId)).collect(Collectors.toList());
    }

    /**
     * 根据ID获取单个地区
     *
     * @param id
     * @return
     */
    public static AreaCode getAreaCodesById(Integer id) {
        if (id == null) return null;
        return areaCodes.get(areaCodes.indexOf(new AreaCode(id)));
    }

    //-----------------------------地区结束-----------------------------------------

    public static Collection<SystemCode> getAllCodes() {
        return (Collection<SystemCode>) codes.get(Constant.sortCode);
    }


    public static SystemCode getCodeByName(String codeName) {
        return (SystemCode) codes.get(codeName);
    }

    public static Collection<SystemCode> getCodesByType(String codeType) {
        List codeByType = new ArrayList<>();
        for (SystemCode c :
                Constant.getAllCodes()) {
            if (codeType.equals(c.getCodeType())) {
                codeByType.add(c);
            }
        }
        return codeByType;
    }

    public static String getStringCodeValueByName(String codeName) {
        SystemCode codeByName = getCodeByName(codeName);
        if (codeByName == null || codeByName.getCodeValue() == null)
            return null;
        return codeByName.getCodeValue().toString();
    }

    public static Integer getIntegerCodeValueByName(String codeName) {
        SystemCode codeByName = getCodeByName(codeName);
        if (codeByName == null || codeByName.getCodeValue() == null)
            return null;
        return Integer.parseInt(codeByName.getCodeValue().toString());
    }

    public static String getCodeName(String codeType, Object codeValue) {
        Collection<SystemCode> codesByType = getCodesByType(codeType);
        String value = null;
        for (SystemCode systemCode : codesByType) {
            if (systemCode.getCodeValue().equals(codeValue.toString())) {
                value = systemCode.getCodeName();
                break;
            }
        }
        return value;
    }

    public static String getCodeShowName(String codeType, Object codeValue) {
        if (ValidUtil.isEmpty(codeValue)) {
            return null;
        }
        Collection<SystemCode> codesByType = getCodesByType(codeType);
        String value = null;
        for (SystemCode systemCode : codesByType) {
            if (systemCode.getCodeValue().equals(codeValue.toString())) {
                value = systemCode.getCodeShowName();
                break;
            }
        }
        return value;
    }

    public static String getCodeDesc(String codeType, Object codeValue) {
        if (ValidUtil.isEmpty(codeValue)) {
            return null;
        }
        Collection<SystemCode> codesByType = getCodesByType(codeType);
        String value = null;
        for (SystemCode systemCode : codesByType) {
            if (systemCode.getCodeValue().equals(codeValue.toString())) {
                value = systemCode.getCodeDesc();
                break;
            }
        }
        return value;
    }

    public static String getCodesDesByCodes(String typeName, String codes) {
        if (ValidUtil.isEmpty(codes)) {
            return null;
        }
        if (codes.contains(",")) {
            StringBuilder sb = new StringBuilder();
            String[] codesArray = codes.split(",");
            for (String tmp : codesArray) {
                sb.append(Constant.getCodeShowName(typeName, tmp) + ",");
            }
            return sb.substring(0, sb.length() - 1).toString();
        } else {
            return Constant.getCodeShowName(typeName, codes);
        }
    }

    public static String getCodesDesWithParentDesByCodes(String typeName, Object code) {
        if (ValidUtil.isEmpty(code)) {
            return null;
        }
        String codeDes = null;
        String parentId = null;
        for (SystemCode c : Constant.getAllCodes()) {
            if (c.getCodeType().equals(typeName) && c.getCodeValue().equals(code.toString())) {
                codeDes = c.getCodeShowName();
                parentId = c.getCodeParentValue();
                break;
            }
        }
        if (ValidUtil.isEmpty(parentId)) {
            return codeDes;
        }
        for (SystemCode c : Constant.getAllCodes()) {
            if (c.getId().toString().equals(parentId)) {
                codeDes = c.getCodeShowName() + codeDes;
                break;
            }
        }

        return codeDes;
    }

    public static long refreshTime() {
        return lastFreshTimestamp;
    }

    public static String getCityNameById(String cityIds) {
        String subjection = "";
        String[] codeArray = cityIds.split(",");
        try {
            subjection = Constant.getAreaCodesById(Integer.valueOf(codeArray[codeArray.length - 1])).getAreaName();
        } catch (Exception e) {
        }
        return subjection;
    }

    public static Integer getAreaIdByName(String name) {
        for(AreaCode areaCode : areaCodes) {
            if(areaCode.getFullName().equals(name)) {
                return areaCode.getId();
            }
        }
        return null;
    }

    public static String getCodesDesBySimpleName(String typeName, Object codes) {
        if (ValidUtil.isEmpty(codes)) {
            return null;
        }
        if (codes.toString().contains(",")) {
            StringBuilder sb = new StringBuilder();
            String[] codesArray = codes.toString().split(",");
            for (String tmp : codesArray) {
                sb.append(Constant.enumValues.get(typeName + "," + tmp) + ",");
            }
            return sb.substring(0, sb.length() - 1).toString();
        } else {
            return Constant.enumValues.get(typeName + "," + codes);
        }
    }

}
