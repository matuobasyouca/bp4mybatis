package cc.kyp82ndlf.base.entity;

import cc.kyp82ndlf.base.Constant;

/**
 * 返回结果实体
 *
 */
public class ReturnResult {
    private String code;
    private String msg;
    private Object data;

    public ReturnResult(String code,String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ReturnResult() {
    }

    public ReturnResult(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public ReturnResult setData(Object data) {
        this.data = data;
        return this;
    }

    /**
     * 返回成功，data需根据实际情况填入
     * @return
     */
    public static ReturnResult buildSuccessMsg(){
        return buildEnumResult(Constant.StateCode.SUCCESS);
    }

    /**
     * 返回 JSON转换失败信息
     * @return
     */
    public static ReturnResult buildJsonChangeErrorMsg(){
        return buildEnumResult(Constant.StateCode.JSON_CHANGE_ERROR);
    }

    /**
     * 构建返回结果
     * @param codeName
     * @return
     */
    public static ReturnResult buildResult(String codeName){
        SystemCode systemCode = Constant.getCodeByName(codeName);
        return new ReturnResult(systemCode.getCodeName(),systemCode.getCodeShowName(),null);
    }

    /**
     * 根据传入枚举类型，获取描述
     * @param em
     * @return
     */
    public static ReturnResult buildEnumResult(Enum em){
        try {
            return new ReturnResult(em.getClass().getDeclaredField("codeName").get(em).toString(),
                    em.getClass().getDeclaredField("desc").get(em).toString(), null);
        } catch (Exception e) {
            return new ReturnResult(Constant.StateCode.ERROR.codeName, Constant.StateCode.ERROR.desc,null);
        }
    }
}
