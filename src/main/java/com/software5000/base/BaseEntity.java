package com.software5000.base;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 基础实体类 <br/>
 * 注意：<br/>
 * 1. 非数据库字段，需要以 <b>@NotDatabaseField</b> <code>( not database field )</code>标注，这样在自动映射时才不会认成数据库字段。 <br/>
 */
public class BaseEntity implements Serializable {

    /**
     *
     */

    @NotDatabaseField
    private static final long serialVersionUID = 1L;

    Integer id;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    Timestamp createTime = null;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    Timestamp updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}