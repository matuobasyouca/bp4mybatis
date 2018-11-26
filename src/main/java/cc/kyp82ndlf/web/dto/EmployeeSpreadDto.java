package cc.kyp82ndlf.web.dto;

public class EmployeeSpreadDto {

    /**
     * 周新增用户
     */
    private Integer weekNum;
    /**
     * 周新增付费
     */
    private Double weekMoney;
    /**
     * 月新增用户
     */
    private Integer monthNum;
    /**
     * 月新增付费
     */
    private Double monthMoney;

    public Integer getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(Integer weekNum) {
        this.weekNum = weekNum;
    }

    public Double getWeekMoney() {
        return weekMoney;
    }

    public void setWeekMoney(Double weekMoney) {
        this.weekMoney = weekMoney;
    }

    public Integer getMonthNum() {
        return monthNum;
    }

    public void setMonthNum(Integer monthNum) {
        this.monthNum = monthNum;
    }

    public Double getMonthMoney() {
        return monthMoney;
    }

    public void setMonthMoney(Double monthMoney) {
        this.monthMoney = monthMoney;
    }
}
