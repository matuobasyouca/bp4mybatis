package com.software5000.base;

import com.software5000.util.JsqlUtils;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author matuobasyouca@gmail.com
 */
@Repository("baseDaoNew")
public class BaseDaoNew  extends SqlSessionDaoSupport {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 最大的单次批量插入的数量
     */
    private static final int MAX_BATCH_SIZE = 10000;

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

    // region insert 方法块
    /**
     * 简单插入实体对象
     *
     * @param entity 实体对象
     * @throws SQLException
     */
    public <T extends BaseEntity> T insertEntity(T entity) throws SQLException {
        Insert insert = new Insert();
        insert.setTable(new Table(entity.getClass().getSimpleName()));
        insert.setColumns(JsqlUtils.getAllColumnNamesFromEntity(entity.getClass()));
        insert.setItemsList(JsqlUtils.getAllColumnValueFromEntity(entity,insert.getColumns()));

        Map<String, Object> param = new HashMap<>();
        param.put("baseSql", insert.toString());
        param.put("entity", entity);
        this.insert("BaseDao.insertEntity", param);

        return entity;
    }


    /**
     * 简单批量插入实体对象
     *
     * @param entitys
     * @throws SQLException
     */
    public List insertEntityList(List<? extends BaseEntity> entitys) throws SQLException {
        if (entitys == null || entitys.size() == 0) {
            return null;
        }

        Insert insert = new Insert();
        insert.setTable(new Table(entitys.get(0).getClass().getSimpleName()));
        insert.setColumns(JsqlUtils.getAllColumnNamesFromEntity(entitys.get(0).getClass()));
        MultiExpressionList multiExpressionList = new MultiExpressionList();
        entitys.stream().map(e -> JsqlUtils.getAllColumnValueFromEntity(e,insert.getColumns())).forEach(e -> multiExpressionList.addExpressionList(e));
        insert.setItemsList(multiExpressionList);

        Map<String, Object> param = new MapperMethod.ParamMap<>();
        param.put("baseSql", insert.toString());
        param.put("list", entitys);
        this.insert("BaseDao.insertEntityList", param);
        return entitys;
    }
    // endregion

    // region delete 方法块
    /**
     * 根据sql方法名称和对象id
     */
    public void delete(String sqlName, Object id) throws SQLException {
        getSqlSession().delete(sqlName, id);
    }


    /**
     * 简单删除实体对象
     *
     * @param entity 实体对象
     * @throws SQLException
     */
    public <T extends BaseEntity> void deleteEntity(T entity) throws SQLException {
        this.deleteEntity(entity.getId(),entity.getClass());
    }

    /**
     * 简单删除实体对象
     *
     * @param entity 实体对象
     * @throws SQLException
     */
    public <T extends BaseEntity> void deleteEntity(Integer id, Class<T> entity) throws SQLException {
        Delete delete = new Delete();
        delete.setTable(new Table(entity.getClass().getSimpleName()));
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column("id"));
        equalsTo.setRightExpression(new LongValue(id));
        delete.setWhere(equalsTo);

        Map<String, Object> param = new HashMap<>();
        param.put("baseSql", delete.toString());
        this.delete("BaseDao.deleteEntity", param);
    }
    // endregion


    // region update 方法块

    // endregion

    // region select 方法块

    // endregion
}
