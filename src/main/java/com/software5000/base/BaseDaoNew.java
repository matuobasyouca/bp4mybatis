package com.software5000.base;

import com.software5000.util.JsqlUtils;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author matuobasyouca@gmail.com
 */
@Repository("baseDaoNew")
public class BaseDaoNew  extends SqlSessionDaoSupport {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String PARAM_PREFIX = "p_prefix";

    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }


    /**
     * 根据sql方法名称和对象插入数据库
     */
    public Object insert(String sqlName, Object obj) throws SQLException {
        return getSqlSession().insert(sqlName, obj);

    }

    /**
     * 简单插入实体对象
     *
     * @param entity 实体对象
     * @throws SQLException
     */
    public <T extends BaseEntity> T insertEntity(T entity) throws SQLException {
        Insert insert = new Insert();
        insert.setTable(new Table(entity.getClass().getSimpleName()));
        insert.setColumns(JsqlUtils.getColumnNameFromEntity(entity.getClass(),true));
        insert.setItemsList(JsqlUtils.getAllColumnValueFromEntity(entity,insert.getColumns()));

        Map<String, Object> param = new HashMap<>();
        param.put("baseSql", insert.toString());
        param.put(PARAM_PREFIX, entity);
        this.insert("BaseDao.insertEntity", param);
        logger.info(insert.toString());

        return entity;
    }

    /**
     * 模仿PreparedStatement防止sql注入
     *
     * @param str
     * @return
     */
    private String transactSQLInjection(Object str) {
        if (str == null) {
            return "null";
        }
        return "'" + str.toString().trim().replace("'", "").replace("\\", "\\\\").replace(";", "；") + "'";
    }
}
